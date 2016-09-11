package net.vpc.app.vainruling.core.service.content;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

/**
 * Created by vpc on 9/11/16.
 */
@Controller
@Scope(value = "singleton")
public class SimpleNotificationService implements NotificationTextService{
    @Override
    public void loadContentTexts(String name) {

    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ContentText> getContentTextListHead(String id, int max) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}
