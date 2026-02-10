package com.bisberrios.notifications.application;

import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NotificationEventBus {

    private final NotificationClient client;
    private final List<Consumer<NotificationResult>> subscribers = new ArrayList<>();

    public NotificationEventBus(NotificationClient client) {
        this.client = client;
    }

    public void subscribe(Consumer<NotificationResult> listener) {
        subscribers.add(listener);
    }

    public void publish(Notification notification) {
        // Se ejecuta en un nuevo hilo para no bloquear el publicador
        CompletableFuture.supplyAsync(() -> client.send(notification))
                .thenAccept(result -> subscribers.forEach(s -> s.accept(result)));
    }

}
