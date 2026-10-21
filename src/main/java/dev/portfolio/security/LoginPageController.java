package dev.portfolio.security;

import dev.portfolio.content.SiteContentService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginPageController {
    private final SiteContentService contentService;

    public LoginPageController(SiteContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            Model model,
            CsrfToken csrfToken) {
        model.addAttribute("content", contentService.getContent());
        model.addAttribute("loginError", error != null);
        model.addAttribute("_csrf", csrfToken);
        return "login";
    }
}
