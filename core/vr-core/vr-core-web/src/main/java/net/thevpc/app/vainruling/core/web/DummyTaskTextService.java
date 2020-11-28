package net.thevpc.app.vainruling.core.web;

import net.thevpc.app.vainruling.VrTaskTextService;
import net.thevpc.app.vainruling.core.service.content.ContentText;

import java.util.Collections;
import java.util.List;

public class DummyTaskTextService implements VrTaskTextService {

    @Override
    public int getSupport(String name) {
        return 0;
    }

    @Override
    public int getActiveCount() {
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
