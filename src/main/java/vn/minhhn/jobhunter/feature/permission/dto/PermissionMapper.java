package vn.minhhn.jobhunter.feature.permission.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.minhhn.jobhunter.feature.permission.domain.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "id", ignore = true)
    Permission toEntity(CreatePermissionRequest dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdatePermissionRequest dto, @MappingTarget Permission entity);

    PermissionResponse toResponseDto(Permission entity);
}