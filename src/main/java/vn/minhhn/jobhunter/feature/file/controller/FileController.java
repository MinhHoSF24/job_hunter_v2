package vn.minhhn.jobhunter.feature.file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.minhhn.jobhunter.feature.file.dto.FileUploadResponse;
import vn.minhhn.jobhunter.feature.file.service.FileService;
import vn.minhhn.jobhunter.shared.dto.common.ApiResponse;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {
        FileUploadResponse response = fileService.upload(file, folder);
        String s3Url = fileService.uploadFileToS3(file, folder);
        response = new FileUploadResponse(
                response.fileName(),
                response.folder(),
                s3Url, // Cập nhật URL trả về thành URL S3
                response.size(),
                response.uploadedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("File uploaded", response));
    }
}
