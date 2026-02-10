package com.bisberrios.notifications.application.port;

import com.bisberrios.notifications.domain.Notification;
import com.bisberrios.notifications.domain.NotificationResult;

public interface NotificationProvider<T extends Notification> {

    NotificationResult send(T notification);
    boolean supports(Notification notification);

}
