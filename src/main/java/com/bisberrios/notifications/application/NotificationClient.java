package com.bisberrios.notifications.application;

import com.bisberrios.notifications.application.port.NotificationProvider;
import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.RetryConfig;
import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Builder
public class NotificationClient {

    @Singular
    private final List<NotificationProvider<?>> providers;

    @Builder.Default
    private final RetryConfig retryConfig = RetryConfig.defaultConfigs();

    public NotificationResult send(Notification notification) {
        log.info("Buscando proveedor para: {}", notification.getClass().getSimpleName());

        return providers.stream()
                .filter(p -> p.supports(notification))
                .findFirst()
                .map(p -> sendWithRetry((NotificationProvider<Notification>) p, notification))
                .orElseGet(() -> {
                    log.warn("No se encontró un proveedor para la notificación a: {}", notification.getRecipient());
                    return new NotificationResult(false, null, "No provider found", "None");
                });
    }

    /**
     * Orquesta los reintentos basados en la RetryConfig inyectada.
     */
    private NotificationResult sendWithRetry(NotificationProvider<Notification> provider, Notification notification) {
        NotificationResult result = null;
        int attempts = 0;

        while (attempts < retryConfig.getMaxAttempts()) {
            attempts++;
            log.debug("Intento {} de {} para enviar notificación vía {}",
                    attempts, retryConfig.getMaxAttempts(), provider.getClass().getSimpleName());

            result = provider.send(notification);

            if (result.success()) {
                return result;
            }

            if (attempts < retryConfig.getMaxAttempts()) {
                log.warn("Intento {} fallido. Reintentando en {}ms...", attempts, retryConfig.getDelayMillis());
                try {
                    Thread.sleep(retryConfig.getDelayMillis());
                } catch (InterruptedException e) {
                    log.error("Hebra interrumpida durante el reintento", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.error("Se agotaron los {} intentos permitidos sin éxito.", retryConfig.getMaxAttempts());
        return result;
    }

    public List<NotificationResult> sendBatch(List<Notification> notifications) {
        log.info("Iniciando envío masivo de {} notificaciones", notifications.size());
        return notifications.stream()
                .map(this::send)
                .toList();
    }

}
