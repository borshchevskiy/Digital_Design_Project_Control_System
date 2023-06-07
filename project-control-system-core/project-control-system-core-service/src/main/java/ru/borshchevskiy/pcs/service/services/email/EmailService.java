package ru.borshchevskiy.pcs.service.services.email;

import ru.borshchevskiy.pcs.entities.task.Task;

public interface EmailService {

    void sendTemplateEmail(String to, String subject, String htmlEmailContent);

    void sendNewTaskNotification(Task task);

}
