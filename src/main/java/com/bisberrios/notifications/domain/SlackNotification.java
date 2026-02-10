package com.bisberrios.notifications.domain;

import com.bisberrios.notifications.domain.exception.NotificationValidationException;

public record SlackNotification(String webhookUrl, Content content) implements Notification {

    public SlackNotification {
        if (webhookUrl == null) {
            throw new NotificationValidationException("ID de canal de Slack es requerido).");
        }
        if (!webhookUrl.contains("hooks.slack.com/services/")) {
            throw new NotificationValidationException("La URL de Webhook es inválida o no secreta.");
        }
    }

    @Override
    public String getRecipient() {
        return webhookUrl;
    }

    @Override
    public String getContent() {
        return (content instanceof SimpleContent s) ? s.text() : "Rich Block Content";
    }

}
