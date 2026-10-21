package dev.portfolio.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {
    @Mock
    private ContactMessageRepository repository;
    @Mock
    private FeedbackNotificationService notificationService;

    @Test
    void savesSubmittedMessageAndReturnsSuccessMessage() {
        ContactMessage message = new ContactMessage();
        message.setName("Visitor");
        message.setEmail("visitor@example.com");
        message.setMessage("Hello from the portfolio.");
        when(repository.save(message)).thenReturn(message);
        when(notificationService.sendFeedbackNotification(message)).thenReturn(true);

        Map<String, String> response = new ContactController(repository, notificationService).submit(message);

        verify(repository).save(message);
        verify(notificationService).sendFeedbackNotification(message);
        assertThat(response).containsEntry("message", "Pesan berhasil dikirim. Terima kasih!");
    }

    @Test
    void reportsWhenMessageIsSavedButEmailNotificationCannotBeSent() {
        ContactMessage message = new ContactMessage();
        message.setName("Visitor");
        message.setEmail("visitor@example.com");
        message.setMessage("Hello from the portfolio.");
        when(repository.save(message)).thenReturn(message);
        when(notificationService.sendFeedbackNotification(message)).thenReturn(false);

        Map<String, String> response = new ContactController(repository, notificationService).submit(message);

        verify(repository).save(message);
        assertThat(response).containsEntry(
            "message",
            "Pesan tersimpan, tetapi notifikasi email belum terkirim. Coba lagi nanti.");
    }
}
