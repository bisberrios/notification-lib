package com.bisberrios.notifications.domain;

import com.bisberrios.notifications.domain.exception.NotificationValidationException;

public record PushNotification(String deviceToken, String title, Content content) implements Notification {

    public PushNotification {
        if (deviceToken == null || deviceToken.isBlank()) {
            throw new NotificationValidationException("El token del dispositivo es obligatorio.");
        }
        if (title == null || title.isBlank()) {
            throw new NotificationValidationException("El título de la notificación push es obligatorio.");
        }
    }

    @Override
    public String getRecipient() {
        return deviceToken;
    }

    @Override
    public String getContent() {
        String body = (content instanceof SimpleContent s) ? s.text() : "Template: " + ((TemplateContent)content).templateName();
        return String.format("[Push] Title: %s | Body: %s", title, body);
    }

}
