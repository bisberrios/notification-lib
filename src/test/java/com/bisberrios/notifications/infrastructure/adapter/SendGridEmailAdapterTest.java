package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.domain.EmailNotification;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SimpleContent;
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
class SendGridEmailAdapterTest {

    @Mock
    private ContentProcessor processor;

    private SendGridEmailAdapter adapter;
    private final String apiKey = "SG.1234567890abcdefghij";
    private final String verifiedFrom = "no-reply@test.com";

    @BeforeEach
    void setUp() {
        adapter = new SendGridEmailAdapter(apiKey, processor, verifiedFrom);
    }

    @Test
    @DisplayName("Constructor: Debe lanzar excepción si la API Key es nula o vacía")
    void constructorValidationTest() {
        // Test nulo
        assertThrows(NotificationValidationException.class, () ->
                new SendGridEmailAdapter(null, processor, verifiedFrom));

        // Test vacío
        assertThrows(NotificationValidationException.class, () ->
                new SendGridEmailAdapter("   ", processor, verifiedFrom));
    }

    @Test
    @DisplayName("Send: Debe enviar email exitosamente y generar ID con prefijo sg-")
    void sendSuccessTest() {
        // GIVEN
        EmailNotification notification = new EmailNotification(
                "destinatario@test.com",
                "Asunto Test",
                new SimpleContent("Cuerpo del mensaje")
        );
        when(processor.process(any())).thenReturn("Contenido Procesado");

        // WHEN
        NotificationResult result = adapter.send(notification);

        // THEN
        assertTrue(result.success());
        assertNotNull(result.messageId());
        assertTrue(result.messageId().startsWith("sg-"));
        assertEquals("SendGrid", result.providerName());
        assertNull(result.error());
    }

    @Test
    @DisplayName("Send: Debe capturar errores durante el procesamiento y retornar fallo")
    void sendFailureTest() {
        // GIVEN
        EmailNotification notification = new EmailNotification(
                "destinatario@test.com",
                "Asunto Test",
                new SimpleContent("Error")
        );
        // Forzamos la excepción para cubrir el bloque catch
        when(processor.process(any())).thenThrow(new RuntimeException("Error de conexión"));

        // WHEN
        NotificationResult result = adapter.send(notification);

        // THEN
        assertFalse(result.success());
        assertEquals("Error de conexión", result.error());
        assertNull(result.messageId());
    }

    @Test
    @DisplayName("Supports: Debe soportar solo EmailNotification")
    void supportsTest() {
        EmailNotification email = new EmailNotification("a@b.com", "S", new SimpleContent("C"));
        assertTrue(adapter.supports(email));

        // Se puede probar con otra implementación de Notification para asegurar el false
        assertFalse(adapter.supports(null));
    }

}
