package ru.borshchevskiy.pcs.service.services.email.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.service.services.email.EmailService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.templates.path}")
    private String templateLocation;
    // TODO: Устанавливается режим отправки писем, обычнчый или тестовый.
    //  При тестовом реальный адрес получателя меняется на тестовый. После согласования убрать
    @Value("${spring.mail.sending-mode}")
    private String emailSendingMode;
    // TODO:  Адрес тестового получателя писем.  После согласования убрать
    @Value("${spring.mail.test.recipient}")
    private String testRecipient;
    private final String NEW_TASK_SUBJECT = "New task assigned";
    private final String NEW_TASK_DEFAULT_MESSAGE = "A new task has been assigned to you!";

//    TODO: Метод отправляет письмо простым текстом без шаблонизатора. После согласования ДЗ убрать
//    @Override
//    @EventListener
//    public void sendSimpleTaskNotification(Task task) {
//        String toAddress = task.getImplementer().getEmail();
//
//        if (!StringUtils.hasText(toAddress)) {
//            log.error("Failed to send email notification on new task. " +
//                      "Reason: " + task.getImplementer().getFirstname() + " " + task.getImplementer().getLastname() +
//                      "has no email provided.");
//            return;
//        }
//
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo(toAddress);
//        simpleMailMessage.setSubject(NEW_TASK_SUBJECT);
//        simpleMailMessage.setText(NEW_TASK_DEFAULT_MESSAGE);
//
//        // TODO: Согласно ДЗ выполняется подмена адреса получателя на тестовый (мой),
//        //  а реальный включается в тело письма. Заменить после согласования
//        if ("test".equalsIgnoreCase(emailSendingMode)) {
//
//            simpleMailMessage.setTo(testRecipient);
//            simpleMailMessage.setText("Hello, " + toAddress + "! " + NEW_TASK_DEFAULT_MESSAGE);
//        }
//
//
//        try {
//            emailSender.send(simpleMailMessage);
//            log.info("Email notification on new task sent to " + toAddress);
//
//        } catch (MailException exception) {
//
//            log.error("Failed to send email to " + toAddress + ". " +
//                      "Reason: " + exception.getCause() + ". Message: " + exception.getMessage());
//
//        }
//    }

    @Override
    @Async
    @EventListener
    public void sendTemplateTaskNotification(Task task) {

        String toAddress = task.getImplementer().getEmail();

        if (!StringUtils.hasText(toAddress)) {
            log.error("Failed to send email notification on new task. " +
                      "Reason: " + task.getImplementer().getFirstname() + " " + task.getImplementer().getLastname() +
                      "has no email provided.");
            return;
        }

        Context context = new Context();
        Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("firstname", task.getImplementer().getFirstname());
        templateContext.put("lastname", task.getImplementer().getLastname());
        templateContext.put("projectName", task.getProject().getName());
        templateContext.put("taskName", task.getName());
        templateContext.put("taskDescription", task.getDescription());
        templateContext.put("deadline", task.getDeadline().toLocalDate());
        templateContext.put("laborCosts", task.getLaborCosts());
        templateContext.put("authorName", task.getAuthor().getFirstname() + " " + task.getAuthor().getLastname());
        templateContext.put("taskId", task.getId());

        context.setVariables(templateContext);
        String emailContent = templateEngine.process(templateLocation, context);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());


            // TODO: Заменить отправку на тестовый адрес на отправку на реальный адрес
            //  mimeMessageHelper.setTo(toAddress);
            mimeMessageHelper.setTo(testRecipient);
            mimeMessageHelper.setSubject(NEW_TASK_SUBJECT);
            mimeMessageHelper.setText(emailContent, true);

            emailSender.send(message);

            log.info("Email notification on new task sent to " + toAddress);

        } catch (MailException | MessagingException exception) {
            log.error("Failed to send email to " + toAddress + ". " +
                      "Reason: " + exception.getCause() + ". Message: " + exception.getMessage());
        }

    }
}