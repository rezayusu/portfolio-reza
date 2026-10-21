package dev.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class TemplateController {
    
    @GetMapping("/template1")
    public String template(Model model) {
        String msg = "Welcome to Thymeleaf Template";
        // adding the attribute(key-value pair)
        model.addAttribute("message", msg);
        // returning the view name
        return "Index";
    }
}
