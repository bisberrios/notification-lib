package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.port.NotificationProvider;
import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FailingAdapter implements NotificationProvider<Notification> {

    @Override
    public NotificationResult send(Notification notification) {
        log.debug("Intentando enviar (y fallando) a: {}", notification.getRecipient());
        return new NotificationResult(false, null, "Error de red persistente", "FailingService");
    }

    @Override
    public boolean supports(Notification notification) {
        return true;
    }

}
