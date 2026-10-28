package dev.portfolio.project;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "portfolio_projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String description;

    @NotBlank @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String category;

    @Size(max = 500)
    @Column(length = 500)
    private String imageUrl;

    @Size(max = 2000)
    @Column(length = 2000)
    private String galleryUrls = "";

    @Size(max = 500)
    @Column(length = 500)
    private String projectUrl;

    @Size(max = 300)
    @Column(length = 300)
    private String techStack;

    @Column(nullable = false)
    private boolean featured = false;

    @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
    @Column(length = 7)
    private String accentColor = "#f4d66d";

    public Project() {}

    public Project(String title, String description, String category, String imageUrl,
                   String projectUrl, String techStack, boolean featured) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.projectUrl = projectUrl;
        this.techStack = techStack;
        this.featured = featured;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public String getGalleryUrls() { return galleryUrls; }
    public String getProjectUrl() { return projectUrl; }
    public String getTechStack() { return techStack; }
    public boolean isFeatured() { return featured; }
    public String getAccentColor() { return accentColor == null ? "#f4d66d" : accentColor; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setGalleryUrls(String galleryUrls) { this.galleryUrls = galleryUrls; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }
}
