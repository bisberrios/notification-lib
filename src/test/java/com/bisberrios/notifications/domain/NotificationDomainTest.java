package com.bisberrios.notifications.domain;

import com.bisberrios.notifications.domain.exception.NotificationValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationDomainTest {

    @Test
    @DisplayName("EmailNotification debe lanzar excepción si el formato de correo/subject es inválido")
    void emailValidationTest() {
        assertAll(
                () -> assertThrows(NotificationValidationException.class, () ->
                        new EmailNotification(null, "Asunto", new SimpleContent("Cuerpo"))),
                () -> assertThrows(NotificationValidationException.class, () ->
                        new EmailNotification("correo_invalido", "Asunto", new SimpleContent("Cuerpo"))),
                () -> assertThrows(NotificationValidationException.class, () ->
                        new EmailNotification("test@test.com", null, new SimpleContent("Cuerpo"))),
                () -> assertThrows(NotificationValidationException.class, () ->
                        new EmailNotification("test@test.com", "", new SimpleContent("Cuerpo")))
        );
    }

    @Test
    @DisplayName("SmsNotification debe lanzar excepción si falta el teléfono o no cumple formato")
    void smsValidationTest() {
        assertAll(
                () -> assertThrows(NotificationValidationException.class, () ->
                        new SmsNotification(null, new SimpleContent("Texto"))),
                () -> assertThrows(NotificationValidationException.class, () ->
                        new SmsNotification("", new SimpleContent("Texto")))
        );
    }

    @Test
    @DisplayName("PushNotification debe lanzar excepción si falta el título o el token")
    void pushValidationTest() {
        assertAll(
                () -> assertThrows(NotificationValidationException.class, () ->
                        new PushNotification("", "Título", new SimpleContent("Cuerpo"))),
                () -> assertThrows(NotificationValidationException.class, () ->
                        new PushNotification("token", " ", new SimpleContent("Cuerpo")))
        );
    }

    @Test
    @DisplayName("SlackNotification debe lanzar excepción si falta el canal o es inválido")
    void slackValidationTest() {
        assertAll(
                () -> assertThrows(NotificationValidationException.class, () ->
                        new SlackNotification(null, new SimpleContent("Cuerpo"))),
                () -> assertThrows(NotificationValidationException.class, () ->
                        new SlackNotification("abc", new SimpleContent("Cuerpo")))
        );
    }

}
