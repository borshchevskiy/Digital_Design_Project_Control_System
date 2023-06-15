package ru.borshchevskiy.pcs.service.services.email.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.service.services.email.EmailService;
import ru.borshchevskiy.pcs.service.services.email.templates.TemplateProcessor;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${app.mail.test.recipient:#{null}}")
    private String testRecipient;
    private final JavaMailSender mailSender;
    private final TemplateProcessor templateProcessor;

    @Override
    @Async
    @EventListener
    public void receiveNewTaskEvent(Task task) {
        sendNewTaskNotification(task);
    }

    @Override
    @RabbitListener(id = "newTask", queues = "app.task.new")
    public void receiveNewTaskMessage(Task task) {
        sendNewTaskNotification(task);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendNewTaskNotification(Task task) {
        String toAddress = task.getImplementer().getEmail();

        if (!StringUtils.hasText(toAddress)) {
            log.error("Failed to send email notification on new task. " +
                      "Reason: " + task.getImplementer().getFirstname() + " " + task.getImplementer().getLastname() +
                      "has no email provided.");
            return;
        }

        String emailContent = templateProcessor.prepareNewTaskTemplate(task);

        // Если в properties указан тестовый email, то отправка идет на него
        if (testRecipient != null) {
            toAddress = testRecipient;
        }

        sendTemplateEmail(toAddress, "New task assigned", emailContent);
    }

    @Override
    public void sendTemplateEmail(String toAddress, String subject, String htmlEmailContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            mimeMessageHelper.setTo(toAddress);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlEmailContent, true);

            mailSender.send(message);

            log.debug("Email notification on new task sent to " + toAddress);

        } catch (MailException | MessagingException exception) {
            log.error("Failed to send email to " + toAddress + ". " +
                      "Reason: " + exception.getCause() + ". Message: " + exception.getMessage(), exception);
        }
    }

}
