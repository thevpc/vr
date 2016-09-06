package net.vpc.app.vainruling.core.service.content;

import net.vpc.app.vainruling.core.service.model.AppUser;

import java.util.Collections;
import java.util.List;

/**
 * Created by vpc on 9/5/16.
 */
public interface ContentTextService {
    public void loadArticles(String name);

    public List<ContentText> getArticlesList(String id);
}
