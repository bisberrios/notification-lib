package com.bisberrios.notifications.application;

import com.bisberrios.notifications.application.port.TemplateEngine;
import com.bisberrios.notifications.domain.SimpleContent;
import com.bisberrios.notifications.domain.TemplateContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentProcessorTest {

    @Mock
    private TemplateEngine templateEngine;
    private ContentProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ContentProcessor(templateEngine);
    }

    @Test
    void shouldReturnRawTextForSimpleContent() {
        SimpleContent content = new SimpleContent("Texto plano");
        String result = processor.process(content);
        assertEquals("Texto plano", result);
        verifyNoInteractions(templateEngine);
    }

    @Test
    void shouldInvokeEngineForTemplateContent() {
        TemplateContent content = new TemplateContent("bienvenida", Map.of("user", "Bismarck"));
        when(templateEngine.render(content)).thenReturn("Hola Bismarck");

        String result = processor.process(content);

        assertEquals("Hola Bismarck", result);
        verify(templateEngine, times(1)).render(content);
    }

}
