package dev.portfolio.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dev.portfolio.certificate.CertificateRepository;
import dev.portfolio.contact.ContactMessageRepository;
import dev.portfolio.content.ImageStorageService;
import dev.portfolio.content.SiteContent;
import dev.portfolio.content.SiteContentService;
import dev.portfolio.education.EducationRepository;
import dev.portfolio.project.ProjectRepository;
import dev.portfolio.toolbox.ToolboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.WebDataBinder;

class AdminControllerContentBindingTest {
    private static class TestSiteContent extends SiteContent {}

    @Test
    void allowsGitHubUrlAndRejectsRemovedTikTokField() {
        AdminController controller = new AdminController(
            mock(SiteContentService.class),
            mock(ProjectRepository.class),
            mock(ContactMessageRepository.class),
            mock(ImageStorageService.class),
            mock(ToolboxRepository.class),
            mock(EducationRepository.class),
            mock(CertificateRepository.class));
        SiteContent content = new TestSiteContent();
        WebDataBinder binder = new WebDataBinder(content, "content");
        controller.bindContent(binder);

        binder.bind(new MutablePropertyValues()
            .add("githubUrl", "https://github.com/reza")
            .add("tiktokUrl", "https://tiktok.com/@reza"));

        assertThat(content.getGithubUrl()).isEqualTo("https://github.com/reza");
        assertThat(content.getTiktokUrl()).isEmpty();
        assertThat(binder.getBindingResult().getSuppressedFields()).contains("tiktokUrl");
    }
}
