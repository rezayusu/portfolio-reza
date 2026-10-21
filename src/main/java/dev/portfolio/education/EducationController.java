package dev.portfolio.education;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EducationController {
    private final EducationRepository repository;

    public EducationController(EducationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/education")
    public List<EducationEntry> listEducation() {
        return repository.findAllByOrderByStartYearAscIdAsc();
    }
}
