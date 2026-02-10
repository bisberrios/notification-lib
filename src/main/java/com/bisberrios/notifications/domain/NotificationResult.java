package com.bisberrios.notifications.domain;

public record NotificationResult(

        boolean success,
        String messageId,
        String error,
        String providerName

) {}
