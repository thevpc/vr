package net.vpc.app.vainruling;

import net.vpc.app.vainruling.core.service.content.NotificationText;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrNotificationTextService extends VrContentTextService {

    public void publish(NotificationText notificationText);

    public void unpublish(int id);

    public int getUnreadCount();
}
