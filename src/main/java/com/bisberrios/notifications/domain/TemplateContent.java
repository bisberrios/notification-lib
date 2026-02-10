package com.bisberrios.notifications.domain;

import java.util.Map;

public record TemplateContent(String templateName, Map<String, Object> variables) implements Content {}
