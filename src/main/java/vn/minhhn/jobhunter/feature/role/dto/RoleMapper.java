package vn.minhhn.jobhunter.feature.role.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.minhhn.jobhunter.feature.permission.dto.PermissionMapper;
import vn.minhhn.jobhunter.feature.role.domain.Role;

@Mapper(componentModel = "spring", uses = { PermissionMapper.class })
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    Role toEntity(CreateRoleRequest dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateRoleRequest dto, @MappingTarget Role entity);

    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toResponseDto(Role role);
}