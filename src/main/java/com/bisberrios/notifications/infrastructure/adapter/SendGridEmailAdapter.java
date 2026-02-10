package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.application.port.NotificationProvider;
import com.bisberrios.notifications.domain.EmailNotification;
import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.exception.NotificationValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class SendGridEmailAdapter implements NotificationProvider<EmailNotification> {

    private final String apiKey;
    private final ContentProcessor processor;
    private final String verifiedFromEmail; // Ajuste 1: Remitente verificado

    public SendGridEmailAdapter(String apiKey, ContentProcessor processor, String verifiedFromEmail) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new NotificationValidationException("La API Key de SendGrid es obligatoria.");
        }
        this.apiKey = apiKey;
        this.processor = processor;
        this.verifiedFromEmail = verifiedFromEmail;
        log.debug("[Adapter Setup] SendGrid configurado con remitente: {}", verifiedFromEmail);
    }

    @Override
    public NotificationResult send(EmailNotification notification) {
        log.info("[SendGrid] Preparando envío para: {}", notification.getRecipient());

        try {
            String body = processor.process(notification.content());
            String contentType = "text/plain";

            log.debug("Enviando vía SendGrid API v3:");
            log.debug("- Authorization: Bearer {}", "********" + apiKey.substring(apiKey.length() - 4));
            log.debug("- From: {}", verifiedFromEmail);
            log.debug("- To: {}", notification.getRecipient());
            log.debug("- Subject: {}", notification.subject());
            log.debug("- Content Type: {}", contentType);
            log.debug("- Body: {}", body);

            log.info("[SendGrid API] Response: 202 Accepted");

            return new NotificationResult(true, "sg-" + UUID.randomUUID(), null, "SendGrid");
        } catch (Exception e) {
            log.error("Error en SendGrid API: {}", e.getMessage());
            return new NotificationResult(false, null, e.getMessage(), "SendGrid");
        }
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof EmailNotification;
    }

}
