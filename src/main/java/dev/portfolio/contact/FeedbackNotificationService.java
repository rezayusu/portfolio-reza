package dev.portfolio.contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class FeedbackNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackNotificationService.class);

    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final String senderPassword;
    private final String recipientAddress;

    public FeedbackNotificationService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String senderAddress,
            @Value("${spring.mail.password:}") String senderPassword,
            @Value("${portfolio.notifications.feedback-recipient}") String recipientAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
        this.senderPassword = senderPassword;
        this.recipientAddress = recipientAddress;
    }

    public boolean sendFeedbackNotification(ContactMessage message) {
        if (senderAddress.isBlank() || senderPassword.isBlank()) {
            logger.warn("Feedback email notification was not sent because SMTP credentials are not configured.");
            return false;
        }

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(senderAddress);
        email.setTo(recipientAddress);
        email.setSubject("New portfolio feedback");
        email.setText(
            "You received a new message from your portfolio.\n\n"
                + "Name: " + message.getName() + "\n"
                + "Email: " + message.getEmail() + "\n\n"
                + message.getMessage());

        try {
            mailSender.send(email);
            return true;
        } catch (MailException exception) {
            logger.error("Could not send portfolio feedback notification email.", exception);
            return false;
        }
    }
}
