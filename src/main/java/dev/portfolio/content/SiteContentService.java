package dev.portfolio.content;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteContentService {
    private static final long CONTENT_ID = 1L;

    private final SiteContentRepository repository;
    private final ProfilePhotoRepository profilePhotoRepository;

    public SiteContentService(SiteContentRepository repository, ProfilePhotoRepository profilePhotoRepository) {
        this.repository = repository;
        this.profilePhotoRepository = profilePhotoRepository;
    }

    @Transactional
    public SiteContent getContent() {
        return repository.findById(CONTENT_ID).orElseGet(() -> repository.save(new SiteContent()));
    }

    @Transactional
    public SiteContent update(SiteContent input) {
        return updateWithProfilePhotos(input, null, false, null, false);
    }

    @Transactional
    public SiteContent updateWithProfilePhotos(
            SiteContent input,
            ProfilePhotoData primaryPhoto,
            boolean clearPrimaryPhoto,
            ProfilePhotoData secondaryPhoto,
            boolean clearSecondaryPhoto) {
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
        content.setAboutHoverAudioUrl(input.getAboutHoverAudioUrl());
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
        updateProfilePhoto(ProfilePhoto.PRIMARY_SLOT, primaryPhoto, clearPrimaryPhoto);
        updateProfilePhoto(ProfilePhoto.SECONDARY_SLOT, secondaryPhoto, clearSecondaryPhoto);
        return repository.save(content);
    }

    @Transactional(readOnly = true)
    public ProfilePhoto getProfilePhoto(String slot) {
        return profilePhotoRepository.findById(slot).orElse(null);
    }

    private void updateProfilePhoto(String slot, ProfilePhotoData photo, boolean clearPhoto) {
        if (clearPhoto) {
            profilePhotoRepository.deleteById(slot);
        } else if (photo != null) {
            profilePhotoRepository.save(new ProfilePhoto(slot, photo.contentType(), photo.data()));
        }
    }
}
