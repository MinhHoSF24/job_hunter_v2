package vn.minhhn.jobhunter.feature.role.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.minhhn.jobhunter.feature.permission.domain.Permission;
import vn.minhhn.jobhunter.feature.permission.repository.PermissionRepository;
import vn.minhhn.jobhunter.feature.role.domain.Role;
import vn.minhhn.jobhunter.feature.role.dto.CreateRoleRequest;
import vn.minhhn.jobhunter.feature.role.dto.RoleMapper;
import vn.minhhn.jobhunter.feature.role.dto.RoleResponse;
import vn.minhhn.jobhunter.feature.role.dto.UpdateRoleRequest;
import vn.minhhn.jobhunter.feature.role.repository.RoleRepository;
import vn.minhhn.jobhunter.shared.exception.DuplicateResourceException;
import vn.minhhn.jobhunter.shared.exception.ResourceNotFoundException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository,
            RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Vai trò", "name", request.name());
        }

        List<Permission> permissions = resolvePermissions(request.permissionIds());
        var role = roleMapper.toEntity(request);
        role.setPermissions(permissions);
        var savedRole = roleRepository.save(role);
        return roleMapper.toResponseDto(savedRole);
    }

    @Transactional
    public RoleResponse update(UpdateRoleRequest request) {
        var role = roleRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "id", request.id()));

        if (roleRepository.existsByNameAndIdNot(request.name(), request.id())) {
            throw new DuplicateResourceException("Vai trò", "name", request.name());
        }

        List<Permission> permissions = resolvePermissions(request.permissionIds());
        roleMapper.updateEntityFromDto(request, role);
        role.setPermissions(permissions);
        var savedRole = roleRepository.save(role);
        return roleMapper.toResponseDto(savedRole);
    }

    public RoleResponse getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò", "id", id));
        return roleMapper.toResponseDto(role);
    }

    public Page<RoleResponse> getAll(int page, int size) {
        return roleRepository.findAll(PageRequest.of(page, size))
                .map(roleMapper::toResponseDto);
    }

    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vai trò", "id", id);
        }
        roleRepository.deleteById(id);
    }

    private List<Permission> resolvePermissions(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> uniqueIds = permissionIds.stream().distinct().toList();
        List<Permission> found = permissionRepository.findAllById(uniqueIds);

        if (found.size() != uniqueIds.size()) {
            Set<Long> foundIds = found.stream().map(Permission::getId).collect(Collectors.toSet());
            Long missingId = uniqueIds.stream().filter(id -> !foundIds.contains(id)).findFirst().orElseThrow();
            throw new ResourceNotFoundException("Quyền hạn", "id", missingId);
        }

        return found;
    }
}
