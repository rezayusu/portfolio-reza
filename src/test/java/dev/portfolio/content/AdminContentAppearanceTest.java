package dev.portfolio.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.portfolio.admin.AdminController;
import dev.portfolio.certificate.CertificateRepository;
import dev.portfolio.contact.ContactMessageRepository;
import dev.portfolio.education.EducationRepository;
import dev.portfolio.project.ProjectRepository;
import dev.portfolio.toolbox.ToolboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class AdminContentAppearanceTest {
    @Mock
    private SiteContentService contentService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ContactMessageRepository messageRepository;
    @Mock
    private ImageStorageService imageStorage;
    @Mock
    private ToolboxRepository toolboxRepository;
    @Mock
    private EducationRepository educationRepository;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AdminController controller;

    @Test
    void editingPageContentPreservesSavedAppearanceColors() {
        SiteContent existing = new SiteContent();
        existing.setPrimaryColor("#123456");
        existing.setAccentColor("#abcdef");
        existing.setBackgroundColor("#fedcba");
        existing.setAdminLightText("#111111");
        existing.setAdminDarkPanel("#222222");
        when(contentService.getContent()).thenReturn(existing);

        SiteContent input = new SiteContent();
        input.setDisplayName("Updated name");
        input.setPrimaryColor("#ff8a76");
        input.setAccentColor("#a9c8a5");
        input.setBackgroundColor("#f6f0e5");

        controller.updateContent(
            input,
            new BeanPropertyBindingResult(input, "content"),
            false,
            false,
            false,
            new MockMultipartFile("photo", new byte[0]),
            new MockMultipartFile("secondPhoto", new byte[0]),
            new MockMultipartFile("logoPhoto", new byte[0]),
            mock(Model.class),
            mock(CsrfToken.class),
            redirectAttributes);

        ArgumentCaptor<SiteContent> savedContent = ArgumentCaptor.forClass(SiteContent.class);
        verify(contentService).update(savedContent.capture());
        assertThat(savedContent.getValue().getDisplayName()).isEqualTo("Updated name");
        assertThat(savedContent.getValue().getPrimaryColor()).isEqualTo("#123456");
        assertThat(savedContent.getValue().getAccentColor()).isEqualTo("#abcdef");
        assertThat(savedContent.getValue().getBackgroundColor()).isEqualTo("#fedcba");
        assertThat(savedContent.getValue().getAdminLightText()).isEqualTo("#111111");
        assertThat(savedContent.getValue().getAdminDarkPanel()).isEqualTo("#222222");
    }

    @Test
    void updatingAppearanceSavesAdminDashboardColors() {
        SiteContent existing = new SiteContent();
        when(contentService.getContent()).thenReturn(existing);

        controller.updateAppearance(
            "#123456", "#234567", "#345678",
            "#456789", "#56789a", "#6789ab", "#789abc",
            "#89abcd", "#9abcde", "#abcdef", "#bcdef0",
            redirectAttributes);

        ArgumentCaptor<SiteContent> savedContent = ArgumentCaptor.forClass(SiteContent.class);
        verify(contentService).update(savedContent.capture());
        assertThat(savedContent.getValue().getPrimaryColor()).isEqualTo("#123456");
        assertThat(savedContent.getValue().getAdminLightText()).isEqualTo("#456789");
        assertThat(savedContent.getValue().getAdminLightSidebar()).isEqualTo("#56789a");
        assertThat(savedContent.getValue().getAdminLightHover()).isEqualTo("#6789ab");
        assertThat(savedContent.getValue().getAdminLightPanel()).isEqualTo("#789abc");
        assertThat(savedContent.getValue().getAdminDarkText()).isEqualTo("#89abcd");
        assertThat(savedContent.getValue().getAdminDarkSidebar()).isEqualTo("#9abcde");
        assertThat(savedContent.getValue().getAdminDarkHover()).isEqualTo("#abcdef");
        assertThat(savedContent.getValue().getAdminDarkPanel()).isEqualTo("#bcdef0");
    }
}
