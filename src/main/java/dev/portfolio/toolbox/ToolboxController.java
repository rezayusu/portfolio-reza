package dev.portfolio.toolbox;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ToolboxController {
    private final ToolboxRepository repository;

    public ToolboxController(ToolboxRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/toolbox")
    public List<ToolboxItem> listPublicTools() {
        return repository.findAllByOrderByIdAsc();
    }
}
