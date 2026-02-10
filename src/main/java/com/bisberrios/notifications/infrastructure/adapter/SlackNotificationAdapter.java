package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.application.port.NotificationProvider;
import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SlackNotification;
import com.bisberrios.notifications.domain.exception.NotificationValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlackNotificationAdapter implements NotificationProvider<SlackNotification> {

    private final String webhookUrl;
    private final ContentProcessor processor;

    public SlackNotificationAdapter(String webhookUrl, ContentProcessor processor) {
        if (webhookUrl == null || !webhookUrl.startsWith("https://hooks.slack.com/services/")) {
            throw new NotificationValidationException("La URL de Webhook de Slack es obligatoria y debe ser válida.");
        }
        this.webhookUrl = webhookUrl;
        this.processor = processor;
    }

    @Override
    public NotificationResult send(SlackNotification notification) {
        log.info("[Slack] Realizando POST a la Webhook URL: {}", notification.getRecipient());

        try {
            String message = processor.process(notification.content());
            log.debug("Payload JSON enviado: {\"text\": \"{}\"}", message);
            return new NotificationResult(true, "slack-" + System.currentTimeMillis(), null, "Slack");
        } catch (Exception e) {
            return new NotificationResult(false, null, "invalid_payload", "Slack");
        }
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof SlackNotification;
    }

}
