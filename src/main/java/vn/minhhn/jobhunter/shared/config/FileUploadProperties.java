package vn.minhhn.jobhunter.shared.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class FileUploadProperties {

    private String baseDir;
    private long maxFileSize;
    private List<String> allowedFileTypes;
    private List<String> allowedFolders;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public List<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(List<String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public List<String> getAllowedFolders() {
        return allowedFolders;
    }

    public void setAllowedFolders(List<String> allowedFolders) {
        this.allowedFolders = allowedFolders;
    }
}
