package vn.minhhn.jobhunter.feature.user.dto;

import java.time.Instant;
import java.util.List;

import vn.minhhn.jobhunter.feature.company.dto.CompanyResponse;
import vn.minhhn.jobhunter.feature.role.dto.RoleResponse;

public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private int age;
    private String address;
    private String gender;
    private String avatar;
    private Instant createdAt;
    private Instant updatedAt;
    private CompanyResponse company;
    private List<RoleResponse> roles;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String name, String email, int age, String address, String gender, String avatar, Instant createdAt, Instant updatedAt, CompanyResponse company, List<RoleResponse> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.company = company;
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public CompanyResponse getCompany() {
        return company;
    }

    public void setCompany(CompanyResponse company) {
        this.company = company;
    }

    public List<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResponse> roles) {
        this.roles = roles;
    }
}
