package com.bisberrios.notifications.application;

import com.bisberrios.notifications.application.port.TemplateEngine;
import com.bisberrios.notifications.domain.Content;
import com.bisberrios.notifications.domain.SimpleContent;
import com.bisberrios.notifications.domain.TemplateContent;

public class ContentProcessor {

    private final TemplateEngine templateEngine;

    public ContentProcessor(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String process(Content content) {
        if (content instanceof SimpleContent simple) {
            return simple.text();
        } else if (content instanceof TemplateContent template) {
            return templateEngine.render(template);
        }
        throw new IllegalArgumentException("Unknown content type");
    }

}
