package net.vpc.app.vainruling.core.service.content;

/**
 * Created by vpc on 9/5/16.
 */
public interface NotificationTextService extends ContentTextService {
    public void publish(NotificationText notificationText);
    public void unpublish(int id) ;
    public int getUnreadCount();
}
