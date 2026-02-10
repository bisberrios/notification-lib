package com.bisberrios.notifications.domain;

import com.bisberrios.notifications.domain.exception.NotificationValidationException;

import java.util.regex.Pattern;

public record EmailNotification(String recipient, String subject, Content content) implements Notification {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public EmailNotification {
        if (recipient == null || !EMAIL_PATTERN.matcher(recipient).matches()) {
            throw new NotificationValidationException("Formato de email inválido para el destinatario.");
        }
        if (subject == null || subject.isBlank()) {
            throw new NotificationValidationException("El asunto del correo no puede estar vacío.");
        }
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public String getContent() {
        String body = (content instanceof SimpleContent s) ? s.text() : "Template: " + ((TemplateContent)content).templateName();
        return String.format("[Email] Subject: %s | Body: %s", subject, body);
    }

}
