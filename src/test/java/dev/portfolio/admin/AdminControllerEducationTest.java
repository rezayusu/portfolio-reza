package dev.portfolio.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import dev.portfolio.certificate.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import dev.portfolio.contact.ContactMessageRepository;
import dev.portfolio.content.ImageStorageService;
import dev.portfolio.content.SiteContentService;
import dev.portfolio.education.EducationEntry;
import dev.portfolio.education.EducationRepository;
import dev.portfolio.project.ProjectRepository;
import dev.portfolio.toolbox.ToolboxRepository;

@ExtendWith(MockitoExtension.class)
class AdminControllerEducationTest {
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
    void savesUploadedSchoolDocumentationWhenCreatingEducation() {
        EducationEntry entry = education("SMK", "Vocational School");
        entry.setWebsiteUrl("https://school.example");
        MockMultipartFile photo = new MockMultipartFile(
            "photo", "school.jpg", "image/jpeg", new byte[] {1});
        when(imageStorage.store(photo)).thenReturn("/uploads/school.jpg");

        String view = controller.createEducation(
            entry, new BeanPropertyBindingResult(entry, "newEducation"), photo, redirectAttributes);

        ArgumentCaptor<EducationEntry> savedEntry = ArgumentCaptor.forClass(EducationEntry.class);
        verify(educationRepository).save(savedEntry.capture());
        assertThat(view).isEqualTo("redirect:/admin/education");
        assertThat(savedEntry.getValue().getImageUrl()).isEqualTo("/uploads/school.jpg");
        assertThat(savedEntry.getValue().getWebsiteUrl()).isEqualTo("https://school.example");
    }

    @Test
    void replacesSchoolDocumentationWhenUpdatingEducation() {
        EducationEntry existing = education("SMA", "Old School");
        existing.setImageUrl("/uploads/old-school.jpg");
        EducationEntry input = education("SMK", "New School");
        input.setWebsiteUrl("https://new-school.example");
        MockMultipartFile photo = new MockMultipartFile(
            "photo", "new-school.jpg", "image/jpeg", new byte[] {1});
        when(educationRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(imageStorage.store(photo)).thenReturn("/uploads/new-school.jpg");

        String view = controller.updateEducation(
            7L, input, new BeanPropertyBindingResult(input, "education"), photo, redirectAttributes);

        verify(educationRepository).save(existing);
        assertThat(view).isEqualTo("redirect:/admin/education");
        assertThat(existing.getSchoolName()).isEqualTo("New School");
        assertThat(existing.getImageUrl()).isEqualTo("/uploads/new-school.jpg");
        assertThat(existing.getWebsiteUrl()).isEqualTo("https://new-school.example");
    }

    @Test
    void keepsSchoolDocumentationWhenUpdatingWithoutNewPhoto() {
        EducationEntry existing = education("SMK", "Vocational School");
        existing.setImageUrl("/uploads/school.jpg");
        EducationEntry input = education("SMK", "Updated School");
        when(educationRepository.findById(7L)).thenReturn(Optional.of(existing));

        controller.updateEducation(
            7L, input, new BeanPropertyBindingResult(input, "education"), null, redirectAttributes);

        assertThat(existing.getImageUrl()).isEqualTo("/uploads/school.jpg");
    }

    @Test
    void loadsMessagesInDashboardInboxInNewestFirstOrder() {
        Model model = org.mockito.Mockito.mock(Model.class);
        CsrfToken csrfToken = org.mockito.Mockito.mock(CsrfToken.class);
        when(messageRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        String view = controller.messagesDashboard(model, csrfToken);

        assertThat(view).isEqualTo("admin");
        verify(model).addAttribute("activePage", "messages");
        verify(model).addAttribute("messages", List.of());
        verify(messageRepository).findAllByOrderByCreatedAtDesc();
    }

    private EducationEntry education(String level, String schoolName) {
        EducationEntry entry = new EducationEntry();
        entry.setLevel(level);
        entry.setSchoolName(schoolName);
        entry.setStartYear(2021);
        return entry;
    }
}
