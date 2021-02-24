package net.thevpc.app.vainruling.core.web;

import net.thevpc.app.vainruling.VrTaskTextService;

import java.util.Collections;
import java.util.List;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

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
    public List<VrContentText> getContentTextList(String id) {
        return Collections.emptyList();
    }

    @Override
    public List<VrContentText> getContentTextListHead(String id, int max) {
        return Collections.emptyList();
    }
}
