package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.domain.TemplateContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTemplateAdapterTest {

    private SimpleTemplateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SimpleTemplateAdapter();
    }

    @Test
    @DisplayName("Render: Debe retornar el String formateado con el nombre y variables de la plantilla")
    void renderTest() {
        // GIVEN
        Map<String, Object> variables = Map.of("user", "Bismarck", "points", 100);
        TemplateContent content = new TemplateContent("welcome_email", variables);

        // WHEN
        String result = adapter.render(content);

        // THEN
        assertNotNull(result);
        assertTrue(result.contains("Procesando plantilla: welcome_email"));
        assertTrue(result.contains("user=Bismarck"));
        assertTrue(result.contains("points=100"));
    }

    @Test
    @DisplayName("Render: Debe funcionar correctamente con mapas de variables vacíos")
    void renderEmptyVariablesTest() {
        // GIVEN
        TemplateContent content = new TemplateContent("simple_alert", Map.of());

        // WHEN
        String result = adapter.render(content);

        // THEN
        assertEquals("Procesando plantilla: simple_alert con variables: {}", result);
    }

}
