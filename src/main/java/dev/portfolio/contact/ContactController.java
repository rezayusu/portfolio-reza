package dev.portfolio.contact;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ContactController {
    private final ContactMessageRepository repository;
    private final FeedbackNotificationService notificationService;

    public ContactController(
            ContactMessageRepository repository,
            FeedbackNotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @PostMapping("/contact")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> submit(@Valid @RequestBody ContactMessage message) {
        ContactMessage savedMessage = repository.save(message);
        boolean notificationSent = notificationService.sendFeedbackNotification(savedMessage);
        String responseMessage = notificationSent
            ? "Pesan berhasil dikirim. Terima kasih!"
            : "Pesan tersimpan, tetapi notifikasi email belum terkirim. Coba lagi nanti.";
        return Map.of("message", responseMessage);
    }

    @GetMapping("/admin/messages")
    public java.util.List<ContactMessage> listMessages() {
        return repository.findAll();
    }
}
