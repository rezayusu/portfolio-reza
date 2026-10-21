package dev.portfolio.education;

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
@Table(name = "portfolio_education")
public class EducationEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String level;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String schoolName;

    @Size(max = 120)
    @Column(length = 120)
    private String program = "";

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer startYear;

    @Min(1900)
    @Max(2100)
    private Integer endYear;

    @Size(max = 600)
    @Column(length = 600)
    private String description = "";

    @Size(max = 500)
    @Column(length = 500)
    private String imageUrl;

    @Size(max = 500)
    @Pattern(regexp = "^(https?://[^\\s]+)?$")
    @Column(length = 500)
    private String websiteUrl;

    public EducationEntry() {}

    public Long getId() { return id; }
    public String getLevel() { return level; }
    public String getSchoolName() { return schoolName; }
    public String getProgram() { return program; }
    public Integer getStartYear() { return startYear; }
    public Integer getEndYear() { return endYear; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getWebsiteUrl() { return websiteUrl; }

    public void setLevel(String level) { this.level = level; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public void setProgram(String program) { this.program = program; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
}
