package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.service.content.ContentText;

import java.util.Collections;
import java.util.List;
import net.vpc.app.vainruling.VrMessageTextService;

public class DummyMessageTextService implements VrMessageTextService {

    @Override
    public int getSupport(String name) {
        return 0;
    }

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
