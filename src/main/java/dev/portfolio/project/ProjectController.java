package dev.portfolio.project;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ProjectController {
    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/projects")
    public List<Project> listPublicProjects() {
        return repository.findAllByOrderByFeaturedDescIdDesc();
    }

    @PostMapping("/admin/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@Valid @RequestBody Project project) {
        return repository.save(project);
    }

    @PutMapping("/admin/projects/{id}")
    public Project update(@PathVariable Long id, @Valid @RequestBody Project input) {
        Project existing = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setTitle(input.getTitle());
        existing.setDescription(input.getDescription());
        existing.setCategory(input.getCategory());
        existing.setImageUrl(input.getImageUrl());
        existing.setProjectUrl(input.getProjectUrl());
        existing.setTechStack(input.getTechStack());
        existing.setFeatured(input.isFeatured());
        existing.setAccentColor(input.getAccentColor());
        return repository.save(existing);
    }

    @DeleteMapping("/admin/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
