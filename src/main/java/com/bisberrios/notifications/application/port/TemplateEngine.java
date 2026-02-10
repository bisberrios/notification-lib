package com.bisberrios.notifications.application.port;

import com.bisberrios.notifications.domain.TemplateContent;

public interface TemplateEngine {

    String render(TemplateContent content);

}
