package net.thevpc.app.vainruling;

import java.util.List;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

/**
 * Created by vpc on 9/5/16.
 */
public interface VrContentTextService {

    int getSupport(String name);
    
    void loadContentTexts(String name);

    List<VrContentText> getContentTextList(String id);

    List<VrContentText> getContentTextListHead(String id, int max);

//    public void setSelectedContentTextById(int id);
}
