package vn.minhhn.jobhunter.feature.role.dto;

import java.time.Instant;
import java.util.List;

import vn.minhhn.jobhunter.feature.permission.dto.PermissionResponse;

public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;
    private Instant createdAt;
    private Instant updatedAt;

    public RoleResponse() {
    }

    public RoleResponse(Long id, String name, String description, List<PermissionResponse> permissions, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PermissionResponse> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionResponse> permissions) {
        this.permissions = permissions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    

//     public static RoleResponse fromEntity(Role role) {
//         List<PermissionResponse> permissionResponses = role.getPermissions().stream()
//                 .map(PermissionResponse::fromEntity)
//                 .toList();
//         return new RoleResponse(
//                 role.getId(),
//                 role.getName(),
//                 role.getDescription(),
//                 permissionResponses,
//                 role.getCreatedAt(),
//                 role.getUpdatedAt()
//         );
}
