package vn.minhhn.jobhunter.feature.file.dto;

import java.time.Instant;

public record FileUploadResponse(
        String fileName,
        String folder,
        String fileUrl,
        long size,
        Instant uploadedAt) {
}
