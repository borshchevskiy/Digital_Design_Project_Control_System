package ru.borshchevskiy.pcs.service.services.email.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.service.listeners.NewTaskListener;
import ru.borshchevskiy.pcs.service.services.email.templates.TemplateProcessor;

@Slf4j
@Service
public class SimpleEmailServiceImpl extends AbstractEmailService implements NewTaskListener {

    // TODO:  Адрес тестового получателя писем.  После согласования убрать
    @Value("${spring.mail.test.recipient}")
    private String testRecipient;
    private final TemplateProcessor templateProcessor;

    @Autowired
    public SimpleEmailServiceImpl(JavaMailSender emailSender, TemplateProcessor templateProcessor) {
        super(emailSender);
        this.templateProcessor = templateProcessor;
    }

    @Override
    @Async
    public void receiveNewTask(Task task) {
        String toAddress = task.getImplementer().getEmail();

        if (!StringUtils.hasText(toAddress)) {
            log.error("Failed to send email notification on new task. " +
                      "Reason: " + task.getImplementer().getFirstname() + " " + task.getImplementer().getLastname() +
                      "has no email provided.");
            return;
        }


        String emailContent = templateProcessor.prepareNewTaskTemplate(task);

        sendTemplateEmail(testRecipient, "New task assigned", emailContent);
    }
}