package com.bisberrios.notifications.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetryConfig {

    @Builder.Default
    private final int maxAttempts = 3; // Máximo de reintentos
    @Builder.Default
    private final long delayMillis = 1000; // Tiempo entre reintentos

    public static RetryConfig defaultConfigs() {
        return RetryConfig.builder().build();
    }

}
