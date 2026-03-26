package vn.minhhn.jobhunter.feature.permission.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.minhhn.jobhunter.feature.permission.domain.Permission;
import vn.minhhn.jobhunter.feature.permission.dto.CreatePermissionRequest;
import vn.minhhn.jobhunter.feature.permission.dto.PermissionMapper;
import vn.minhhn.jobhunter.feature.permission.dto.PermissionResponse;
import vn.minhhn.jobhunter.feature.permission.dto.UpdatePermissionRequest;
import vn.minhhn.jobhunter.feature.permission.repository.PermissionRepository;
import vn.minhhn.jobhunter.shared.exception.DuplicateResourceException;
import vn.minhhn.jobhunter.shared.exception.ResourceNotFoundException;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Transactional
    public PermissionResponse create(CreatePermissionRequest request) {
       if (permissionRepository.existsByApiPathAndMethod(request.apiPath(), request.method())) {
            throw new DuplicateResourceException("Quyền hạn", "apiPath + method",
                    request.apiPath() + " [" + request.method() + "]");
        }
        Permission permission = permissionMapper.toEntity(request);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponseDto(savedPermission);
    }

    @Transactional
    public PermissionResponse update(UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(request.id())
               .orElseThrow(() -> new ResourceNotFoundException("Quyền hạn", "id", request.id()));

        if (permissionRepository.existsByApiPathAndMethodAndIdNot(
                request.apiPath(), request.method(), request.id())) {
            throw new DuplicateResourceException("Quyền hạn", "apiPath + method",
                    request.apiPath() + " [" + request.method() + "]");
        }

        permissionMapper.updateEntityFromDto(request, permission);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponseDto(savedPermission);
    }

    public PermissionResponse getById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quyền hạn", "id", id));
        return permissionMapper.toResponseDto(permission);
    }

    public Page<PermissionResponse> getAll(int page, int size) {
        return permissionRepository.findAll(PageRequest.of(page, size))
                .map(permissionMapper::toResponseDto);
    }

    @Transactional
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quyền hạn", "id", id);
        }
        permissionRepository.deleteById(id);
    }

}
