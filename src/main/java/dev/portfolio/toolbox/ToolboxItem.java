package dev.portfolio.toolbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "portfolio_toolbox")
public class ToolboxItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    @NotBlank
    @Size(max = 16)
    @Column(nullable = false, length = 16)
    private String badge;

    /** Optional image (SVG) URL for the badge icon, sourced from a CDN picker. */
    @Size(max = 255)
    @Column(length = 255)
    private String badgeImageUrl = "";

    /** Optional link visited when the badge sticker is clicked on the public page. */
    @Size(max = 255)
    @Column(length = 255)
    private String badgeLink = "";

    @NotBlank
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
    @Column(nullable = false, length = 7)
    private String accentColor = "#f4d66d";

    public ToolboxItem() {}

    public ToolboxItem(String name, String badge, String accentColor) {
        this.name = name;
        this.badge = badge;
        this.accentColor = accentColor;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBadge() { return badge; }
    public String getBadgeImageUrl() { return badgeImageUrl; }
    public String getBadgeLink() { return badgeLink; }
    public String getAccentColor() { return accentColor; }
    public void setName(String name) { this.name = name; }
    public void setBadge(String badge) { this.badge = badge; }
    public void setBadgeImageUrl(String badgeImageUrl) { this.badgeImageUrl = badgeImageUrl; }
    public void setBadgeLink(String badgeLink) { this.badgeLink = badgeLink; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }
}
