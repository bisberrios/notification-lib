package com.bisberrios.notifications.application;

import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SimpleContent;
import com.bisberrios.notifications.domain.SmsNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventBusTest {

    @Mock
    private NotificationClient client;

    @InjectMocks
    private NotificationEventBus eventBus;

    @Test
    void shouldPublishAndNotifySubscribersAsynchronously() throws InterruptedException {
        // GIVEN
        SmsNotification notification = new SmsNotification("+50588888888", new SimpleContent("Async Test"));
        NotificationResult mockResult = new NotificationResult(true, "async-123", null, "Mock");
        CountDownLatch latch = new CountDownLatch(1);

        when(client.send(any())).thenReturn(mockResult);

        // WHEN
        eventBus.subscribe(result -> {
            if (result.success()) {
                latch.countDown();
            }
        });
        eventBus.publish(notification);

        // THEN
        boolean completed = latch.await(2, TimeUnit.SECONDS); // Espera al hilo asíncrono
        assertTrue(completed, "El suscriptor no fue notificado a tiempo");
        verify(client, times(1)).send(notification);
    }

}
