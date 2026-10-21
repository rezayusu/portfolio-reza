package dev.portfolio.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.portfolio.content.SiteContent;
import dev.portfolio.content.SiteContentService;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;

class LoginPageControllerTest {
    @Test
    void rendersPortfolioContentAndLoginErrorState() {
        SiteContent content = mock(SiteContent.class);
        SiteContentService contentService = mock(SiteContentService.class);
        when(contentService.getContent()).thenReturn(content);
        LoginPageController controller = new LoginPageController(contentService);
        Model model = mock(Model.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        String view = controller.login("invalid", model, csrfToken);

        assertThat(view).isEqualTo("login");
        verify(model).addAttribute("content", content);
        verify(model).addAttribute("loginError", true);
        verify(model).addAttribute("_csrf", csrfToken);
    }
}
