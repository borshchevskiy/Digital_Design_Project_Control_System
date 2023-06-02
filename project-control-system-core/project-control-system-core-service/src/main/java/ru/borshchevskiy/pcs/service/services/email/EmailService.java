package ru.borshchevskiy.pcs.service.services.email;

import ru.borshchevskiy.pcs.entities.task.Task;

public interface EmailService {

    void sendTemplateTaskNotification(Task task);
}
