package com.bisberrios.notifications;

import com.bisberrios.notifications.application.ContentProcessor;
import com.bisberrios.notifications.application.NotificationClient;
import com.bisberrios.notifications.application.NotificationEventBus;
import com.bisberrios.notifications.application.port.TemplateEngine;
import com.bisberrios.notifications.domain.*;
import com.bisberrios.notifications.domain.exception.NotificationValidationException;
import com.bisberrios.notifications.infrastructure.adapter.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Clase de demostración para la librería de notificaciones.
 * Implementa flujos de éxito, asincronía, reintentos y manejo de errores.
 */
@Slf4j
public class NotificationExamples {

    public static void main(String[] args) {
        log.info("=== INICIANDO DEMO COMPLETA: CHALLENGE CRNOVA ===");

        // 1. CONFIGURACIÓN DE COMPONENTES AGNOSTICOS
        TemplateEngine engine = new SimpleTemplateAdapter();
        ContentProcessor processor = new ContentProcessor(engine);

        // 2. CONFIGURACIÓN DE RESILIENCIA (RetryConfig)
        RetryConfig retryConfig = RetryConfig.builder()
                .maxAttempts(3)
                .delayMillis(1000)
                .build();

        // 3. CONFIGURACIÓN DEL CLIENTE (usamos Builder Hexagonal)
        // Se utilizan variables de entorno para mayor seguridad en credenciales
        NotificationClient client = NotificationClient.builder()
                .provider(new SendGridEmailAdapter(
                         "SG.VALID_KEY_123",
                        processor,
                        "no-reply@tu-dominio.com"))
                .provider(new SlackNotificationAdapter("https://hooks.slack.com/services/canal", processor))
                .provider(new TwilioSmsAdapter("AC_FAKE_SID", "+50588888888", processor))
                .retryConfig(retryConfig)
                .build();

        // 4. SETUP DE PUB/SUB (Asincronía)
        NotificationEventBus eventBus = new NotificationEventBus(client);

        eventBus.subscribe(result -> {
            if (result.success()) {
                log.info("[EVENT-BUS] ÉXITO: Proveedor {} | ID: {}", result.providerName(), result.messageId());
            } else {
                log.error("[EVENT-BUS] FALLO FINAL: Proveedor {} | Error: {}", result.providerName(), result.error());
            }
        });

        // ---------------------------------------------------------------------
        // CASO 1: PRUEBA DE FALLO - VALIDACIÓN FAIL-FAST
        // ---------------------------------------------------------------------
        log.info("\n--- [TEST 1: VALIDACIÓN] ---");
        try {
            // Debería fallar por API Key vacía lanzando excepción personalizada
            new SendGridEmailAdapter("", processor, "test@test.com");
        } catch (NotificationValidationException e) {
            log.warn("Capturada validación esperada: {}", e.getMessage());
        }

        // ---------------------------------------------------------------------
        // CASO 2: FLUJO POSITIVO - EMAIL CON PLANTILLA (ASÍNCRONO)
        // ---------------------------------------------------------------------
        log.info("\n--- [TEST 2: ASINCRONÍA Y PLANTILLAS] ---");
        Notification emailTemplate = new EmailNotification(
                "bismarck@ejemplo.com", // Pasa validación Regex
                "Bienvenida",
                new TemplateContent("v1_welcome", Map.of("user", "Bismarck", "puntos", 500))
        );
        eventBus.publish(emailTemplate);

        // ---------------------------------------------------------------------
        // CASO 3: PRUEBA DE RESILIENCIA - REINTENTOS (RETRY LOGIC)
        // ---------------------------------------------------------------------
        log.info("\n--- [TEST 3: REINTENTOS AUTOMÁTICOS] ---");
        NotificationClient clientRetry = NotificationClient.builder()
                .provider(new FailingAdapter())
                .retryConfig(retryConfig)
                .build();

        Notification pushConFallo = new PushNotification(
                "token_dispositivo_xyz",
                "Alerta",
                new SimpleContent("Este mensaje activará la lógica de reintento.")
        );
        clientRetry.send(pushConFallo);

        // ---------------------------------------------------------------------
        // CASO 4: ENVÍO MASIVO (BATCH)
        // ---------------------------------------------------------------------
        log.info("\n--- [TEST 4: ENVÍO MASIVO] ---");
        List<Notification> batch = List.of(
                new SlackNotification("https://hooks.slack.com/services/T0/B0/X0", new SimpleContent("Reporte OK")),
                new EmailNotification("admin@test.com", "Backup", new SimpleContent("Base de datos respaldada"))
        );

        List<NotificationResult> resultadosBatch = client.sendBatch(batch);
        log.info("Batch completado. Resultados exitosos: {}",
                resultadosBatch.stream().filter(NotificationResult::success).count());

        // ---------------------------------------------------------------------
        // CASO 5: CANAL OBLIGATORIO - SMS
        // ---------------------------------------------------------------------
        log.info("\n--- [TEST 5: SMS FORMATO] ---");
        Notification sms = new SmsNotification("+50588888888", new SimpleContent("Su código es: 4455"));
        client.send(sms);

        // ---------------------------------------------------------------------
        // FINALIZACIÓN
        // ---------------------------------------------------------------------
        try {
            log.info("\nEsperando procesamiento de eventos asíncronos...");
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("=== FIN DE LA DEMOSTRACIÓN ===");
    }

}
