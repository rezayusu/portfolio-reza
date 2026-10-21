package dev.portfolio.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.ArgumentCaptor;

class FeedbackNotificationServiceTest {
    private final JavaMailSender mailSender = org.mockito.Mockito.mock(JavaMailSender.class);

    @Test
    void sendsFeedbackToConfiguredRecipient() {
        ContactMessage message = new ContactMessage();
        message.setName("Visitor");
        message.setEmail("visitor@example.com");
        message.setMessage("A suggestion.");
        FeedbackNotificationService service = new FeedbackNotificationService(
            mailSender, "sender@gmail.com", "app-password", "rezayusuf651@gmail.com");

        boolean sent = service.sendFeedbackNotification(message);

        ArgumentCaptor<SimpleMailMessage> email = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(email.capture());
        assertThat(sent).isTrue();
        assertThat(email.getValue().getTo()).containsExactly("rezayusuf651@gmail.com");
        assertThat(email.getValue().getFrom()).isEqualTo("sender@gmail.com");
        assertThat(email.getValue().getReplyTo()).isEqualTo("visitor@example.com");
        assertThat(email.getValue().getText()).contains("visitor@example.com", "A suggestion.");
    }

    @Test
    void doesNotAttemptSendingWithoutSmtpCredentials() {
        FeedbackNotificationService service = new FeedbackNotificationService(
            mailSender, "", "", "rezayusuf651@gmail.com");

        assertThat(service.sendFeedbackNotification(new ContactMessage())).isFalse();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
