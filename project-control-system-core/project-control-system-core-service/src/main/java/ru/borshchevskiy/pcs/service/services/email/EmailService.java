package ru.borshchevskiy.pcs.service.services.email;

public interface EmailService {

    void sendTemplateEmail(String to, String subject, String htmlEmailContent);
}
