package vn.minhhn.jobhunter.feature.company.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.minhhn.jobhunter.feature.company.dto.CompanyResponse;
import vn.minhhn.jobhunter.feature.company.dto.CreateCompanyRequest;
import vn.minhhn.jobhunter.feature.company.dto.UpdateCompanyRequest;
import vn.minhhn.jobhunter.feature.company.service.CompanyService;
import vn.minhhn.jobhunter.shared.dto.common.ApiResponse;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> create(
            @Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.create(request);
        URI location = URI.create("/api/companies/" + response.getId());
        return ResponseEntity.created(location)
                .body(ApiResponse.created("Tạo công ty thành công", response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse response = companyService.update(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật công ty thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CompanyResponse> result = companyService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách công ty thành công", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getById(@PathVariable("id") Long id) {
        CompanyResponse response = companyService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin công ty thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        companyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa công ty thành công", null));
    }
}
