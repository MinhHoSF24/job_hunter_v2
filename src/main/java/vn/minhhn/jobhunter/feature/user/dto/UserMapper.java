package vn.minhhn.jobhunter.feature.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.minhhn.jobhunter.feature.company.dto.CompanyMapper;
import vn.minhhn.jobhunter.feature.role.dto.RoleMapper;
import vn.minhhn.jobhunter.feature.user.domain.User;

@Mapper(componentModel = "spring", uses = { CompanyMapper.class, RoleMapper.class })
public interface UserMapper {
    // @Mapping(target = "gender", expression = "java(User.GenderEnum.valueOf(dto.getGender()))")
    User toEntity(CreateUserDto dto);

    // @Mapping(target = "gender", expression = "java(User.GenderEnum.valueOf(dto.getGender()))")
    void updateEntityFromDto(UpdateUserDto dto, @MappingTarget User entity);

    @Mapping(target = "gender", expression = "java(entity.getGender().name())")
    @Mapping(target = "company", source = "company")
    // @Mapping(target = "company.id", source = "company.id")
    // @Mapping(target = "company.name", source = "company.name")
    @Mapping(target = "roles", source = "roles")
    UserResponseDto toResponseDto(User entity);
}
