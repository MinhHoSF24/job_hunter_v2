package vn.minhhn.jobhunter.feature.auth.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.minhhn.jobhunter.feature.user.domain.User;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterRequest dto);

    RegisterResponse toResponseDto(User request);
}
