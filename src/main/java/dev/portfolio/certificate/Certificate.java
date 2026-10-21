package dev.portfolio.certificate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "portfolio_certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String issuer;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer issuedYear;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description = "";

    @Size(max = 500)
    @Pattern(regexp = "^(https?://[^\\s]+)?$")
    @Column(length = 500)
    private String credentialUrl;

    @Size(max = 500)
    @Column(length = 500)
    private String imageUrl;

    public Certificate() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getIssuer() { return issuer; }
    public Integer getIssuedYear() { return issuedYear; }
    public String getDescription() { return description; }
    public String getCredentialUrl() { return credentialUrl; }
    public String getImageUrl() { return imageUrl; }

    public void setTitle(String title) { this.title = title; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public void setIssuedYear(Integer issuedYear) { this.issuedYear = issuedYear; }
    public void setDescription(String description) { this.description = description; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
