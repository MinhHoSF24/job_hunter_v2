package vn.minhhn.jobhunter.feature.permission.dto;

import java.time.Instant;

public class PermissionResponse {
    private final Long id;
    private final String name;
    private final String apiPath;
    private final String method;
    private final String module;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PermissionResponse(Long id, String name, String apiPath, String method, String module, Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiPath() {
        return apiPath;
    }

    public String getMethod() {
        return method;
    }

    public String getModule() {
        return module;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
