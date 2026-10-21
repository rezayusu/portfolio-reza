package dev.portfolio.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {
    @Mock
    private ContactMessageRepository repository;

    @Test
    void savesSubmittedMessageAndReturnsSuccessMessage() {
        ContactMessage message = new ContactMessage();
        message.setName("Visitor");
        message.setEmail("visitor@example.com");
        message.setMessage("Hello from the portfolio.");

        Map<String, String> response = new ContactController(repository).submit(message);

        verify(repository).save(message);
        assertThat(response).containsEntry("message", "Pesan berhasil dikirim. Terima kasih!");
    }
}
