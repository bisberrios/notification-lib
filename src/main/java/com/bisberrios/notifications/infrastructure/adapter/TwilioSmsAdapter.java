package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.application.port.NotificationProvider;
import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SmsNotification;
import com.bisberrios.notifications.domain.exception.NotificationValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TwilioSmsAdapter implements NotificationProvider<SmsNotification> {

    private final String accountSid;
    private final String fromPhoneNumber;
    private final ContentProcessor processor;

    public TwilioSmsAdapter(String accountSid, String fromPhoneNumber,ContentProcessor processor) {
        if (accountSid == null || !accountSid.startsWith("AC")) {
            throw new NotificationValidationException("Twilio Account SID inválido.");
        }
        this.accountSid = accountSid;
        this.fromPhoneNumber = fromPhoneNumber;
        this.processor = processor;
    }

    @Override
    public NotificationResult send(SmsNotification notification) {
        log.info("[Twilio] Utilizando Account SID: {} para envío", accountSid);
        try {
            String messageText = processor.process(notification.content());

            log.debug("Twilio API Call Simulation:");
            log.debug("From: {}", fromPhoneNumber);
            log.debug("To: {}", notification.getRecipient());
            log.debug("Body: {}", messageText);

            String messageSid = "SM" + UUID.randomUUID().toString().replace("-", "");

            // Retorno de resultados simulados
            return new NotificationResult(true, messageSid, null, "Twilio");
        } catch (Exception e) {
            log.error("[SMS Error] Fallo al procesar mensaje SMS: {}", e.getMessage());
            return new NotificationResult(false, null, e.getMessage(), "Twilio");
        }
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof SmsNotification;
    }

}
