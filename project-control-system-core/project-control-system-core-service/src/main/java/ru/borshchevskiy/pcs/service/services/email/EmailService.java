package ru.borshchevskiy.pcs.service.services.email;

import jakarta.mail.MessagingException;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.Map;

public interface EmailService {

//    TODO: Метод отправляет письмо простым текстом без шаблонизатора. После согласования ДЗ убрать
//    void sendSimpleTaskNotification(Task task);

    void sendTemplateTaskNotification(Task task);
}
