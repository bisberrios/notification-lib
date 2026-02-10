package com.bisberrios.notifications.domain;

public sealed interface Content permits SimpleContent, TemplateContent {}
