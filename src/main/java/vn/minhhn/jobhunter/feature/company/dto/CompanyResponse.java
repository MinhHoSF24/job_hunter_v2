package vn.minhhn.jobhunter.feature.company.dto;


import java.time.Instant;

public class CompanyResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final String address;
    private final String logo;
    private final Instant createdAt;
    private final Instant updatedAt;

    public CompanyResponse(
            Long id,
            String name,
            String description,
            String address,
            String logo,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.logo = logo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getLogo() {
        return logo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    
}
