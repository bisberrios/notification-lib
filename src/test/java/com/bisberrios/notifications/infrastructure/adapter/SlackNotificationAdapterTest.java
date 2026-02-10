package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SimpleContent;
import com.bisberrios.notifications.domain.SlackNotification;
import com.bisberrios.notifications.domain.exception.NotificationValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlackNotificationAdapterTest {

    @Mock
    private ContentProcessor processor;

    private SlackNotificationAdapter adapter;
    private final String validWebhook = "https://hooks.slack.com/services/T000/B000/XXXX";

    @BeforeEach
    void setUp() {
        adapter = new SlackNotificationAdapter(validWebhook, processor);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la URL de Slack es nula o inválida")
    void constructorValidationTest() {
        // Test nulo
        assertThrows(NotificationValidationException.class, () ->
                new SlackNotificationAdapter(null, processor));

        // Test formato inválido
        assertThrows(NotificationValidationException.class, () ->
                new SlackNotificationAdapter("https://google.com", processor));
    }

    @Test
    @DisplayName("Debe enviar la notificación exitosamente")
    void sendSuccessTest() {
        // GIVEN
        SlackNotification notification = new SlackNotification(validWebhook, new SimpleContent("Test Slack"));
        when(processor.process(any())).thenReturn("Mensaje procesado para Slack");

        // WHEN
        NotificationResult result = adapter.send(notification);

        // THEN
        assertTrue(result.success());
        assertTrue(result.messageId().startsWith("slack-"));
        assertEquals("Slack", result.providerName());
        assertNull(result.error());
    }

    @Test
    @DisplayName("Debe retornar error 'invalid_payload' cuando el procesador falla")
    void sendFailureTest() {
        // GIVEN
        SlackNotification notification = new SlackNotification(validWebhook, new SimpleContent("Test Error"));
        // Forzamos la entrada al bloque catch de la clase
        when(processor.process(any())).thenThrow(new RuntimeException("Simulated processing error"));

        // WHEN
        NotificationResult result = adapter.send(notification);

        // THEN
        assertFalse(result.success());
        assertEquals("invalid_payload", result.error());
        assertNull(result.messageId());
    }

    @Test
    @DisplayName("Debe soportar solo instancias de SlackNotification")
    void supportsTest() {
        SlackNotification slack = new SlackNotification(validWebhook, new SimpleContent("text"));
        assertTrue(adapter.supports(slack));
    }

}
