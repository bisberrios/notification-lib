package com.bisberrios.notifications.domain;

public sealed interface Notification permits EmailNotification, SmsNotification, SlackNotification, PushNotification {

    String getRecipient();
    String getContent();

}
