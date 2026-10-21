package dev.portfolio.content;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "portfolio_content")
public class SiteContent {
    @Id
    private Long id = 1L;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String displayName = "Reza";

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String heroLine = "and I make";

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String heroTagline = "things with code.";

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String heroDescription = "A developer in progress collecting little ideas, building useful things, and turning coffee-fueled curiosity into real projects.";

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String aboutHeadline = "Part developer, part creative thinker.";

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String aboutDescription = "I'm learning how to build things for the web, from playful interfaces to applications that solve real problems. I love when thoughtful design meets code that works.";

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String aboutExtraDescription = "This little corner of the internet is my digital scrapbook — a home for experiments, projects, lessons, and happy accidents.";

    @Size(max = 500)
    @Column(length = 500)
    private String aboutPhotoUrl = "";

    @Size(max = 500)
    @Column(length = 500)
    private String aboutPhotoSecondUrl = "";

    @Size(max = 500)
    @Column(length = 500)
    private String logoUrl = "";

    @Size(max = 500)
    @Pattern(regexp = "^(https?://[^\\s]+)?$")
    @Column(length = 500)
    private String whatsappUrl = "";

    @Size(max = 500)
    @Pattern(regexp = "^(https?://[^\\s]+)?$")
    @Column(length = 500)
    private String instagramUrl = "";

    @Size(max = 500)
    @Pattern(regexp = "^(https?://[^\\s]+)?$")
    @Column(length = 500)
    private String tiktokUrl = "";

    @NotBlank
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
    @Column(nullable = false, length = 7)
    private String primaryColor = "#ff8a76";

    @NotBlank
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
    @Column(nullable = false, length = 7)
    private String accentColor = "#a9c8a5";

    @NotBlank
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
    @Column(nullable = false, length = 7)
    private String backgroundColor = "#f6f0e5";

    @Column(length = 7)
    private String adminLightText = "#343a40";

    @Column(length = 7)
    private String adminLightSidebar = "#343a40";

    @Column(length = 7)
    private String adminLightHover = "#2563eb";

    @Column(length = 7)
    private String adminLightPanel = "#ffffff";

    @Column(length = 7)
    private String adminDarkText = "#e5e7eb";

    @Column(length = 7)
    private String adminDarkSidebar = "#111827";

    @Column(length = 7)
    private String adminDarkHover = "#3b82f6";

    @Column(length = 7)
    private String adminDarkPanel = "#1f2937";

    protected SiteContent() {}

    public Long getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getHeroLine() { return heroLine; }
    public String getHeroTagline() { return heroTagline; }
    public String getHeroDescription() { return heroDescription; }
    public String getAboutHeadline() { return aboutHeadline; }
    public String getAboutDescription() { return aboutDescription; }
    public String getAboutExtraDescription() { return aboutExtraDescription; }
    public String getAboutPhotoUrl() { return aboutPhotoUrl; }
    public String getAboutPhotoSecondUrl() { return aboutPhotoSecondUrl; }
    public String getLogoUrl() { return logoUrl; }
    public String getWhatsappUrl() { return whatsappUrl; }
    public String getInstagramUrl() { return instagramUrl; }
    public String getTiktokUrl() { return tiktokUrl; }
    public String getPrimaryColor() { return primaryColor; }
    public String getAccentColor() { return accentColor; }
    public String getBackgroundColor() { return backgroundColor; }
    @JsonIgnore
    public String getAdminLightText() { return adminLightText != null ? adminLightText : "#343a40"; }
    @JsonIgnore
    public String getAdminLightSidebar() { return adminLightSidebar != null ? adminLightSidebar : "#343a40"; }
    @JsonIgnore
    public String getAdminLightHover() { return adminLightHover != null ? adminLightHover : "#2563eb"; }
    @JsonIgnore
    public String getAdminLightPanel() { return adminLightPanel != null ? adminLightPanel : "#ffffff"; }
    @JsonIgnore
    public String getAdminDarkText() { return adminDarkText != null ? adminDarkText : "#e5e7eb"; }
    @JsonIgnore
    public String getAdminDarkSidebar() { return adminDarkSidebar != null ? adminDarkSidebar : "#111827"; }
    @JsonIgnore
    public String getAdminDarkHover() { return adminDarkHover != null ? adminDarkHover : "#3b82f6"; }
    @JsonIgnore
    public String getAdminDarkPanel() { return adminDarkPanel != null ? adminDarkPanel : "#1f2937"; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setHeroLine(String heroLine) { this.heroLine = heroLine; }
    public void setHeroTagline(String heroTagline) { this.heroTagline = heroTagline; }
    public void setHeroDescription(String heroDescription) { this.heroDescription = heroDescription; }
    public void setAboutHeadline(String aboutHeadline) { this.aboutHeadline = aboutHeadline; }
    public void setAboutDescription(String aboutDescription) { this.aboutDescription = aboutDescription; }
    public void setAboutExtraDescription(String aboutExtraDescription) { this.aboutExtraDescription = aboutExtraDescription; }
    public void setAboutPhotoUrl(String aboutPhotoUrl) { this.aboutPhotoUrl = aboutPhotoUrl; }
    public void setAboutPhotoSecondUrl(String aboutPhotoSecondUrl) { this.aboutPhotoSecondUrl = aboutPhotoSecondUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setWhatsappUrl(String whatsappUrl) { this.whatsappUrl = whatsappUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }
    public void setTiktokUrl(String tiktokUrl) { this.tiktokUrl = tiktokUrl; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setAdminLightText(String adminLightText) { this.adminLightText = adminLightText; }
    public void setAdminLightSidebar(String adminLightSidebar) { this.adminLightSidebar = adminLightSidebar; }
    public void setAdminLightHover(String adminLightHover) { this.adminLightHover = adminLightHover; }
    public void setAdminLightPanel(String adminLightPanel) { this.adminLightPanel = adminLightPanel; }
    public void setAdminDarkText(String adminDarkText) { this.adminDarkText = adminDarkText; }
    public void setAdminDarkSidebar(String adminDarkSidebar) { this.adminDarkSidebar = adminDarkSidebar; }
    public void setAdminDarkHover(String adminDarkHover) { this.adminDarkHover = adminDarkHover; }
    public void setAdminDarkPanel(String adminDarkPanel) { this.adminDarkPanel = adminDarkPanel; }
}
