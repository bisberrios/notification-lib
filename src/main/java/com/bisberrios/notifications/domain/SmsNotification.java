package com.bisberrios.notifications.domain;

import com.bisberrios.notifications.domain.exception.NotificationValidationException;

import java.util.regex.Pattern;

public record SmsNotification(String recipient, Content content) implements Notification {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    public SmsNotification {
        if (recipient == null) {
            throw new NotificationValidationException("Número telefónico requerido.");
        }
        if (!PHONE_PATTERN.matcher(recipient).matches()) {
            throw new NotificationValidationException("Formato de número telefónico inválido.");
        }
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public String getContent() {
        return (content instanceof SimpleContent s) ? s.text() : "SMS Template Content";
    }

}
