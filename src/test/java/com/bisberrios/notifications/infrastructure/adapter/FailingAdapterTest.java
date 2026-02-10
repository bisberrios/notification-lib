package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.domain.NotificationResult;
import com.bisberrios.notifications.domain.SimpleContent;
import com.bisberrios.notifications.domain.SmsNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailingAdapterTest {

    private FailingAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FailingAdapter();
    }

    @Test
    @DisplayName("Debe retornar siempre un resultado fallido con error persistente")
    void sendAlwaysFailsTest() {
        // GIVEN
        SmsNotification notification = new SmsNotification("+50588888888", new SimpleContent("Test"));

        // WHEN
        NotificationResult result = adapter.send(notification);

        // THEN
        assertFalse(result.success());
        assertNull(result.messageId());
        assertEquals("Error de red persistente", result.error());
        assertEquals("FailingService", result.providerName());
    }

    @Test
    @DisplayName("Debe soportar cualquier tipo de notificación")
    void supportsEverythingTest() {
        SmsNotification anyNotification = new SmsNotification("+50588888888", new SimpleContent("test"));

        assertTrue(adapter.supports(anyNotification), "FailingAdapter debe retornar true para cualquier notificación");
        assertTrue(adapter.supports(null), "Debe retornar true incluso con null");
    }

}
