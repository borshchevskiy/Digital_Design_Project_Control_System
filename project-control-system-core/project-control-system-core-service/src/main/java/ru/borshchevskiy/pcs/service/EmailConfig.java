package ru.borshchevskiy.pcs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.service.services.email.EmailService;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {

    private final EmailService emailService;

    @Async
    @EventListener
    public void receiveNewTaskEvent(Task task) {
        emailService.sendNewTaskNotification(task);
    }

    @RabbitListener(queues = "app.task.new")
    @ConditionalOnProperty
    public void receiveNewTaskMessage(Task task) {
        emailService.sendNewTaskNotification(task);
    }
}

