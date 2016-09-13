package net.vpc.app.vainruling.core.service.content;

import net.vpc.app.vainruling.core.service.model.AppUser;

import java.util.Collections;
import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface ContentTextService {

    public void loadContentTexts(String name);

    public List<ContentText> getContentTextList(String id);

    public List<ContentText> getContentTextListHead(String id,int max);

//    public void setSelectedContentTextById(int id);
}
