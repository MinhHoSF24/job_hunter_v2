package vn.minhhn.jobhunter.feature.file.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import vn.minhhn.jobhunter.feature.file.dto.FileUploadResponse;
import vn.minhhn.jobhunter.shared.config.FileUploadProperties;
import vn.minhhn.jobhunter.shared.exception.FileUploadException;

@Service
public class FileService {
    private final FileUploadProperties fileUploadProperties;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    public FileService(FileUploadProperties fileUploadProperties, S3Client s3Client) {
        this.fileUploadProperties = fileUploadProperties;
        this.s3Client = s3Client;
    }

    public FileUploadResponse upload(MultipartFile file, String folder) {
        validateFile(file, folder);
        String originalName = file.getOriginalFilename();
        String sanitizedName = sanitizeFileName(originalName);
        String storedName = System.currentTimeMillis() + "_" + sanitizedName;

        Path targetDir = Path.of(fileUploadProperties.getBaseDir(), folder);
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            throw new FileUploadException("Không thể tạo thư mục lưu trữ: " + e.getMessage());
        }

        try {
            Path targetPath = targetDir.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileUploadException("Không thể lưu file: " + e.getMessage());
        }

        String fileUrl = "/uploads/" + folder + "/" + storedName;
        return new FileUploadResponse(storedName, folder, fileUrl, file.getSize(), Instant.now());
    }

    public String uploadFileToS3(MultipartFile file, String folder) {
        // 1. Tạo tên file duy nhất (tránh trùng lặp trên S3)
        validateFile(file, folder);
        String sanitizedFileName = sanitizeFileName(file.getOriginalFilename());
        
        String extension = sanitizedFileName != null ? sanitizedFileName.substring(sanitizedFileName.lastIndexOf("."))
                : "";
        String uniqueFileName = folder + "/" + UUID.randomUUID().toString() + extension;

        // 2. Tạo request đẩy file lên S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .build();

        // 3. Thực thi việc upload
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new FileUploadException("Không thể upload file lên S3: " + e.getMessage());
        }

        // 4. Trả về URL của file (Giả sử bucket của bạn public)
        return "https://" + bucketName + ".s3.amazonaws.com/" + uniqueFileName;
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^A-Za-z0-9._\\-]", "_");
    }

    private void validateFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File không được để trống");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new FileUploadException("Tên file không được để trống");
        }

        String extension = getExtension(originalName);
        List<String> allowedTypes = fileUploadProperties.getAllowedFileTypes();
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            throw new FileUploadException("Lỗi hệ thống: Chưa cấu hình định dạng file cho phép");
        }

        boolean isValid = allowedTypes.stream()
                .anyMatch(type -> type.equalsIgnoreCase(extension));

        if (!isValid) {
            String allowed = String.join(", ", allowedTypes);
            throw new FileUploadException("Định dạng file không được phép. Chỉ chấp nhận: " + allowed);
        }

        if (file.getSize() > fileUploadProperties.getMaxFileSize()) {
            long maxMb = fileUploadProperties.getMaxFileSize() / (1024 * 1024);
            throw new FileUploadException("Kích thước file vượt quá " + maxMb + " MB");
        }

        List<String> allowedFolders = fileUploadProperties.getAllowedFolders();

        if (allowedFolders == null || allowedFolders.isEmpty()) {
            throw new FileUploadException("Lỗi hệ thống: Chưa cấu hình thư mục cho phép");
        }

        boolean isFolderValid = allowedFolders.stream()
                .anyMatch(f -> f.equalsIgnoreCase(folder));

        if (!isFolderValid) {
            String allowed = String.join(", ", allowedFolders);
            throw new FileUploadException("Thư mục không hợp lệ. Chỉ chấp nhận: " + allowed);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1);
    }
}
