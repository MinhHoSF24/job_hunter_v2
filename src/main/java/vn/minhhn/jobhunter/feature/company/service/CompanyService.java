package vn.minhhn.jobhunter.feature.company.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.minhhn.jobhunter.feature.company.domain.Company;
import vn.minhhn.jobhunter.feature.company.dto.CompanyMapper;
import vn.minhhn.jobhunter.feature.company.dto.CompanyResponse;
import vn.minhhn.jobhunter.feature.company.dto.CreateCompanyRequest;
import vn.minhhn.jobhunter.feature.company.dto.UpdateCompanyRequest;
import vn.minhhn.jobhunter.feature.company.repository.CompanyRepository;
import vn.minhhn.jobhunter.shared.exception.DuplicateResourceException;
import vn.minhhn.jobhunter.shared.exception.ResourceNotFoundException;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Transactional
    public CompanyResponse create(CreateCompanyRequest request) {
       if (companyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Công Ty", "name", request.name());
        }
        Company company = companyMapper.toEntity(request);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponseDto(savedCompany);
    }

    @Transactional
    public CompanyResponse update(UpdateCompanyRequest request) {
        Company company = companyRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Công Ty", "id", request.id()));

        if (companyRepository.existsByNameAndIdNot(request.name(), request.id())) {
            throw new DuplicateResourceException("Công Ty", "name", request.name());
        }

        companyMapper.updateEntityFromDto(request, company);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponseDto(savedCompany);
    }

    public CompanyResponse getById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Công Ty", "id", id));
        return companyMapper.toResponseDto(company);
    }

    public Page<CompanyResponse> getAll(int page, int size) {
        return companyRepository.findAll(PageRequest.of(page, size))
                .map(companyMapper::toResponseDto);
    }

    @Transactional
    public void delete(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Công Ty", "id", id);
        }
        companyRepository.deleteById(id);
    }
}
