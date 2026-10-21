package dev.portfolio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class TemplateController2 {
    
    @GetMapping("/template2")
    public String template(Model model) {
        String message = "Top 5 Cloud Service Providers";
        // creating a collection
        List<String> list = new ArrayList<>(); 
        list.add("Amazon Web Services");
        list.add("Microsoft Azure");
        list.add("Google Cloud");
        list.add("Alibaba Cloud");
        list.add("IBM Cloud");
        model.addAttribute("message", message);
        // adding the collection attribute
        model.addAttribute("cloudProvider", list);
        return "index2";
    }
}
