package vn.minhhn.jobhunter.feature.company.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.minhhn.jobhunter.feature.company.domain.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    @Mapping(target = "id", ignore = true)
    Company toEntity(CreateCompanyRequest dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateCompanyRequest dto, @MappingTarget Company entity);

    CompanyResponse toResponseDto(Company entity);
}
