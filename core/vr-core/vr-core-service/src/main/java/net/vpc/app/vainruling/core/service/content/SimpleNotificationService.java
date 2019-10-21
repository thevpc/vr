package net.vpc.app.vainruling.core.service.content;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.vpc.app.vainruling.VrNotificationTextService;

/**
 * Created by vpc on 9/11/16.
 */
@Controller
@Scope(value = "singleton")
public class SimpleNotificationService implements VrNotificationTextService{
    List<NotificationText> notificationTexts=new ArrayList<>();

    @Override
    public int getSupport(String name) {
        return "Notification".equals(name)?1:-1;
    }

    @Override
    public void publish(NotificationText notificationText) {
        for (int i = 0; i < notificationTexts.size(); i++) {
            NotificationText text = notificationTexts.get(i);
            if (text.getId() == notificationText.getId()) {
                notificationTexts.set(i,notificationText);
                return;
            }
        }
        notificationTexts.add(notificationText);
    }

    @Override
    public void unpublish(int id) {
        for (int i = 0; i < notificationTexts.size(); i++) {
            NotificationText text = notificationTexts.get(i);
            if (text.getId() == id) {
                notificationTexts.remove(i);
                return;
            }
        }
    }

    @Override
    public void loadContentTexts(String name) {

    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        return (List)notificationTexts;
    }

    @Override
    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    @Override
    public int getUnreadCount() {
        return notificationTexts.size();
    }
}
