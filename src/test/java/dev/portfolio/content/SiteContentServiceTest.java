package dev.portfolio.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SiteContentServiceTest {
    @Mock
    private SiteContentRepository repository;

    @Test
    void updatesStoredPortfolioCopyAndAppearance() {
        SiteContent existing = new SiteContent();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(SiteContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SiteContent input = new SiteContent();
        input.setDisplayName("Reza Updated");
        input.setHeroLine("I design");
        input.setHeroTagline("thoughtful things.");
        input.setHeroDescription("A new introduction.");
        input.setAboutHeadline("Designer and developer.");
        input.setAboutDescription("A new about paragraph.");
        input.setAboutExtraDescription("One more detail.");
        input.setAboutPhotoUrl("/uploads/profile.png");
        input.setAboutPhotoSecondUrl("/uploads/profile-second.png");
        input.setLogoUrl("/uploads/logo.png");
        input.setWhatsappUrl("https://wa.me/628123456789");
        input.setInstagramUrl("https://instagram.com/rezadev");
        input.setGithubUrl("https://github.com/rezadev");
        input.setPrimaryColor("#112233");
        input.setAccentColor("#445566");
        input.setBackgroundColor("#778899");
        input.setAdminLightText("#101112");
        input.setAdminLightSidebar("#202122");
        input.setAdminLightHover("#303132");
        input.setAdminLightPanel("#404142");
        input.setAdminDarkText("#505152");
        input.setAdminDarkSidebar("#606162");
        input.setAdminDarkHover("#707172");
        input.setAdminDarkPanel("#808182");

        SiteContent saved = new SiteContentService(repository).update(input);

        assertThat(saved.getDisplayName()).isEqualTo("Reza Updated");
        assertThat(saved.getHeroTagline()).isEqualTo("thoughtful things.");
        assertThat(saved.getAboutPhotoUrl()).isEqualTo("/uploads/profile.png");
        assertThat(saved.getAboutPhotoSecondUrl()).isEqualTo("/uploads/profile-second.png");
        assertThat(saved.getLogoUrl()).isEqualTo("/uploads/logo.png");
        assertThat(saved.getWhatsappUrl()).isEqualTo("https://wa.me/628123456789");
        assertThat(saved.getInstagramUrl()).isEqualTo("https://instagram.com/rezadev");
        assertThat(saved.getGithubUrl()).isEqualTo("https://github.com/rezadev");
        assertThat(saved.getPrimaryColor()).isEqualTo("#112233");
        assertThat(saved.getAccentColor()).isEqualTo("#445566");
        assertThat(saved.getBackgroundColor()).isEqualTo("#778899");
        assertThat(saved.getAdminLightText()).isEqualTo("#101112");
        assertThat(saved.getAdminLightSidebar()).isEqualTo("#202122");
        assertThat(saved.getAdminLightHover()).isEqualTo("#303132");
        assertThat(saved.getAdminLightPanel()).isEqualTo("#404142");
        assertThat(saved.getAdminDarkText()).isEqualTo("#505152");
        assertThat(saved.getAdminDarkSidebar()).isEqualTo("#606162");
        assertThat(saved.getAdminDarkHover()).isEqualTo("#707172");
        assertThat(saved.getAdminDarkPanel()).isEqualTo("#808182");
        verify(repository).save(existing);
    }
}
