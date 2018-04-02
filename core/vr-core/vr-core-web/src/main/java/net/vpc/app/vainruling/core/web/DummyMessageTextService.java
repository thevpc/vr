package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.content.MessageTextService;

import java.util.Collections;
import java.util.List;

public class DummyMessageTextService implements MessageTextService {
    @Override
    public int getUnreadCount() {
        return 0;
    }

    @Override
    public void loadContentTexts(String name) {

    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        return Collections.emptyList();
    }

    @Override
    public List<ContentText> getContentTextListHead(String id, int max) {
        return Collections.emptyList();
    }
}
