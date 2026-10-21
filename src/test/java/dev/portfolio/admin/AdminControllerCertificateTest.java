package dev.portfolio.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.portfolio.certificate.Certificate;
import dev.portfolio.certificate.CertificateRepository;
import dev.portfolio.contact.ContactMessageRepository;
import dev.portfolio.content.ImageStorageService;
import dev.portfolio.content.SiteContentService;
import dev.portfolio.education.EducationRepository;
import dev.portfolio.project.ProjectRepository;
import dev.portfolio.toolbox.ToolboxRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class AdminControllerCertificateTest {
    @Mock private SiteContentService contentService;
    @Mock private ProjectRepository projectRepository;
    @Mock private ContactMessageRepository messageRepository;
    @Mock private ImageStorageService imageStorage;
    @Mock private ToolboxRepository toolboxRepository;
    @Mock private EducationRepository educationRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AdminController controller;

    @Test
    void savesCertificateAndUploadedImage() {
        Certificate certificate = certificate("Java Fundamentals");
        MockMultipartFile photo = new MockMultipartFile(
            "photo", "certificate.jpg", "image/jpeg", new byte[] {1});
        when(imageStorage.store(photo)).thenReturn("/uploads/certificate.jpg");

        String view = controller.createCertificate(
            certificate, new BeanPropertyBindingResult(certificate, "newCertificate"), photo, redirectAttributes);

        ArgumentCaptor<Certificate> saved = ArgumentCaptor.forClass(Certificate.class);
        verify(certificateRepository).save(saved.capture());
        assertThat(view).isEqualTo("redirect:/admin/certificates");
        assertThat(saved.getValue().getTitle()).isEqualTo("Java Fundamentals");
        assertThat(saved.getValue().getImageUrl()).isEqualTo("/uploads/certificate.jpg");
    }

    @Test
    void updatesCertificateAndKeepsItsImageWhenNoReplacementIsUploaded() {
        Certificate existing = certificate("Old certificate");
        existing.setImageUrl("/uploads/certificate.jpg");
        Certificate input = certificate("Updated certificate");
        input.setImageUrl("/uploads/certificate.jpg");
        when(certificateRepository.findById(8L)).thenReturn(Optional.of(existing));

        String view = controller.updateCertificate(
            8L, input, new BeanPropertyBindingResult(input, "certificate"), null, redirectAttributes);

        verify(certificateRepository).save(existing);
        assertThat(view).isEqualTo("redirect:/admin/certificates");
        assertThat(existing.getTitle()).isEqualTo("Updated certificate");
        assertThat(existing.getImageUrl()).isEqualTo("/uploads/certificate.jpg");
    }

    private Certificate certificate(String title) {
        Certificate certificate = new Certificate();
        certificate.setTitle(title);
        certificate.setIssuer("Learning Provider");
        certificate.setIssuedYear(2025);
        certificate.setDescription("Completed a course.");
        certificate.setCredentialUrl("https://example.com/verify");
        return certificate;
    }
}
