package net.vpc.app.vainruling.core.service.content;

import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface ContentTextService {

    void loadContentTexts(String name);

    List<ContentText> getContentTextList(String id);

    List<ContentText> getContentTextListHead(String id, int max);

//    public void setSelectedContentTextById(int id);
}
