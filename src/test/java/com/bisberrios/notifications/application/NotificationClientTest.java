package com.bisberrios.notifications.application;

import com.bisberrios.notifications.application.port.NotificationProvider;
import com.bisberrios.notifications.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private NotificationProvider<EmailNotification> emailProvider;

    @Test
    @DisplayName("Debe reintentar el envío según la configuración cuando el proveedor falla")
    void shouldRetryBasedOnConfigWhenProviderFails() {
        // GIVEN: 3 intentos permitidos
        RetryConfig retryConfig = RetryConfig.builder()
                .maxAttempts(3)
                .delayMillis(10)
                .build();

        NotificationClient client = NotificationClient.builder()
                .provider(emailProvider)
                .retryConfig(retryConfig)
                .build();

        EmailNotification notification = new EmailNotification(
                "test@crnova.com",
                "Prueba",
                new SimpleContent("Mensaje")
        );

        // Simulamos que el proveedor soporta la notificación pero falla en el envío
        when(emailProvider.supports(notification)).thenReturn(true);
        when(emailProvider.send(notification))
                .thenReturn(new NotificationResult(false, null, "Error temporal", "MockProvider"));

        // WHEN
        NotificationResult result = client.send(notification);

        // THEN: Se verifican exactamente 3 intentos
        verify(emailProvider, times(3)).send(notification);
        assertFalse(result.success());
        assertEquals("Error temporal", result.error());
    }

    @Test
    @DisplayName("Debe retornar error si no se encuentra un proveedor compatible")
    void shouldReturnErrorWhenNoProviderFound() {
        // GIVEN: Cliente sin proveedores registrados
        NotificationClient client = NotificationClient.builder().build();
        SmsNotification sms = new SmsNotification("+50588888888", new SimpleContent("Hola"));

        // WHEN
        NotificationResult result = client.send(sms);

        // THEN
        assertFalse(result.success());
        assertEquals("No provider found", result.error());
    }

    @Test
    void notificationClientBatchCoverageTest() {
        NotificationProvider<Notification> mockProvider = mock(NotificationProvider.class);
        NotificationClient client = NotificationClient.builder().provider(mockProvider).build();

        Notification n1 = new EmailNotification("a@b.com", "S1", new SimpleContent("T1"));
        Notification n2 = new EmailNotification("c@d.com", "S2", new SimpleContent("T2"));

        when(mockProvider.supports(any())).thenReturn(true);
        when(mockProvider.send(any())).thenReturn(new NotificationResult(true, "id", null, "Mock"));

        List<NotificationResult> results = client.sendBatch(List.of(n1, n2));

        assertEquals(2, results.size());
        verify(mockProvider, times(2)).send(any());
    }

}
