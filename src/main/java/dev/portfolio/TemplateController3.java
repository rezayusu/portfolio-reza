package dev.portfolio;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import dev.portfolio.os.OperatingSystem;

@Controller
@RequestMapping("/template3")
public class TemplateController3 {
    
    @GetMapping
    public String template(Model model) {
        model.addAttribute("ops", new OperatingSystem());
        return "index3";
    }
    
    @PostMapping
    public String template( @ModelAttribute("ops") OperatingSystem os , Model model) {
        model.addAttribute("message", os.getOS1()+" "+os.getOS2()+" "+os.getOS3());
        return "index3";
    }
}