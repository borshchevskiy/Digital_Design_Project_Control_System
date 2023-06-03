package ru.borshchevskiy.pcs.service.services.email.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.borshchevskiy.pcs.service.services.email.EmailService;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEmailService implements EmailService {

    private final JavaMailSender emailSender;

    @Override
    public void sendTemplateEmail(String toAddress, String subject, String htmlEmailContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            mimeMessageHelper.setTo(toAddress);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlEmailContent, true);

            emailSender.send(message);

            log.info("Email notification on new task sent to " + toAddress);

        } catch (MailException | MessagingException exception) {
            log.error("Failed to send email to " + toAddress + ". " +
                      "Reason: " + exception.getCause() + ". Message: " + exception.getMessage(), exception);
        }
    }

}
