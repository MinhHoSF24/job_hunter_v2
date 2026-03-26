package vn.minhhn.jobhunter.feature.auth.dto;


import java.time.Instant;

import vn.minhhn.jobhunter.feature.user.domain.User.GenderEnum;

public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;

    public RegisterResponse(Long id, String name, String email, Integer age, GenderEnum gender, String address, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.createdAt = createdAt;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    
}
