package com.bisberrios.notifications.infrastructure.adapter;

import com.bisberrios.notifications.application.port.TemplateEngine;
import com.bisberrios.notifications.domain.TemplateContent;

public class SimpleTemplateAdapter implements TemplateEngine {

    @Override
    public String render(TemplateContent content) {
        return "Procesando plantilla: " + content.templateName() +
                " con variables: " + content.variables();
    }

}
