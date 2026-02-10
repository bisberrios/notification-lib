package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SimpleContent;
import com.bisberrios.notifications.domain.SmsNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwilioSmsAdapterTest {

    @Mock
    private ContentProcessor processor;

    @Test
    @DisplayName("Twilio: Debe enviar SMS exitosamente y generar MessageSID")
    void shouldSendSmsSuccessfully() {
        TwilioSmsAdapter adapter = new TwilioSmsAdapter("AC123", "+15017122661", processor);
        SmsNotification sms = new SmsNotification("+50588888888", new SimpleContent("Hola"));

        when(processor.process(any())).thenReturn("Mensaje Procesado");

        NotificationResult result = adapter.send(sms);

        assertTrue(result.success());
        assertTrue(result.messageId().startsWith("SM"));
        assertEquals("Twilio", result.providerName());
    }

    @Test
    @DisplayName("Twilio: Debe capturar excepciones y retornar resultado fallido")
    void shouldHandleExceptions() {
        TwilioSmsAdapter adapter = new TwilioSmsAdapter("AC123", "+15017122661", processor);
        SmsNotification sms = new SmsNotification("+50588888888", new SimpleContent("Error"));

        when(processor.process(any())).thenThrow(new RuntimeException("API Down"));

        NotificationResult result = adapter.send(sms);

        assertFalse(result.success());
        assertEquals("API Down", result.error());
    }

}
