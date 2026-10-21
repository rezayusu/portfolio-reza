package dev.portfolio.contact;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ContactController {
    private final ContactMessageRepository repository;

    public ContactController(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/contact")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> submit(@Valid @RequestBody ContactMessage message) {
        repository.save(message);
        return Map.of("message", "Pesan berhasil dikirim. Terima kasih!");
    }

    @GetMapping("/admin/messages")
    public java.util.List<ContactMessage> listMessages() {
        return repository.findAll();
    }
}
