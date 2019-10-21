package net.vpc.app.vainruling;

import java.util.List;
import net.vpc.app.vainruling.core.service.content.ContentText;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrContentTextService {

    int getSupport(String name);
    
    void loadContentTexts(String name);

    List<ContentText> getContentTextList(String id);

    List<ContentText> getContentTextListHead(String id, int max);

//    public void setSelectedContentTextById(int id);
}
