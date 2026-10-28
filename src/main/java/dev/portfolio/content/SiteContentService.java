package dev.portfolio.content;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteContentService {
    private static final long CONTENT_ID = 1L;

    private final SiteContentRepository repository;

    public SiteContentService(SiteContentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SiteContent getContent() {
        return repository.findById(CONTENT_ID).orElseGet(() -> repository.save(new SiteContent()));
    }

    @Transactional
    public SiteContent update(SiteContent input) {
        SiteContent content = getContent();
        content.setDisplayName(input.getDisplayName());
        content.setHeroLine(input.getHeroLine());
        content.setHeroTagline(input.getHeroTagline());
        content.setHeroDescription(input.getHeroDescription());
        content.setAboutHeadline(input.getAboutHeadline());
        content.setAboutDescription(input.getAboutDescription());
        content.setAboutExtraDescription(input.getAboutExtraDescription());
        content.setAboutPhotoUrl(input.getAboutPhotoUrl());
        content.setAboutPhotoSecondUrl(input.getAboutPhotoSecondUrl());
        content.setLogoUrl(input.getLogoUrl());
        content.setWhatsappUrl(input.getWhatsappUrl());
        content.setInstagramUrl(input.getInstagramUrl());
        content.setGithubUrl(input.getGithubUrl());
        content.setPrimaryColor(input.getPrimaryColor());
        content.setAccentColor(input.getAccentColor());
        content.setBackgroundColor(input.getBackgroundColor());
        content.setAdminLightText(input.getAdminLightText());
        content.setAdminLightSidebar(input.getAdminLightSidebar());
        content.setAdminLightHover(input.getAdminLightHover());
        content.setAdminLightPanel(input.getAdminLightPanel());
        content.setAdminDarkText(input.getAdminDarkText());
        content.setAdminDarkSidebar(input.getAdminDarkSidebar());
        content.setAdminDarkHover(input.getAdminDarkHover());
        content.setAdminDarkPanel(input.getAdminDarkPanel());
        return repository.save(content);
    }
}
