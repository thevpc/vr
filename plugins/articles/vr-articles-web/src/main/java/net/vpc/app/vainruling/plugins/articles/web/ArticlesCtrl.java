/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.CmsTextDisposition;
import net.vpc.app.vainruling.core.service.content.CmsTextService;
import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.articles.service.ArticlesPlugin;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesFile;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesItem;
import net.vpc.app.vainruling.plugins.articles.service.model.FullArticle;
import net.vpc.upa.*;
import net.vpc.upa.expressions.UserExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Controller
@Scope(value = "singleton")
public class ArticlesCtrl implements CmsTextService {

    @Autowired
    private ArticlesPlugin articles;
    private String[] imageSwitchEffects = new String[]{
            "blindX",
            "blindY",
            "blindZ",
            "cover",
            "curtainX",
            "curtainY",
            "fade",
            "fadeZoom",
            "growX",
            "growY",
            "none",
            "scrollUp",
            "scrollDown",
            "scrollLeft",
            "scrollRight",
            "scrollVert",
            "shuffle",
            "slideX",
            "slideY",
            "toss",
            "turnUp",
            "turnDown",
            "turnLeft",
            "turnRight",
            "uncover",
            "wipe",
            "zoom"
    };

    public ArticlesModel getModel() {
        return VrApp.getBean(ArticlesModel.class);
    }

    public void refresh() {
    }

    public void updateVisit(int articleId) {
        UPA.getPersistenceUnit().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                Entity entity = pu.getEntity(ArticlesItem.class);
                Record record = entity.createRecord();
                record.setObject("visitCount", new UserExpression("visitCount+1"));
                entity.createUpdateQuery()
                        .setValues(record)
                        .byId(articleId)
                        .execute();
            }
        });
    }

    public void loadContentTexts(String name) {
        List<FullArticle> a = findArticles(name);
        getModel().setDisposition(name);
        getModel().getArticles().put(name, a);
        if (getModel().getCurrent() == null) {
            if (a != null && a.size() > 0) {
                getModel().setCurrent(a.get(0));
            } else {
                getModel().setCurrent(null);
            }
        }
    }

    @Override
    public void setContentDisposition(String name) {
        loadContentTexts(name);
    }

    public String getImageSwitchRandomEffect() {
        return imageSwitchEffects[(int) (Math.random() * imageSwitchEffects.length)];
    }

    public List<FullArticle> findArticles(String disposition) {
        UserSession userSession = UserSession.get();
        AppDepartment d = userSession==null?null:userSession.getSelectedDepartment();
        if(d==null){
            d= CorePlugin.get().findDepartment("II");
        }
        AppUser u = UserSession.getCurrentUser();
        return articles.findFullArticlesByUserAndCategory(u == null ? null : u.getLogin(),d==null?-1:d.getId(),true, disposition);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return articles.findArticlesFiles(articleId);
    }

    public FullArticle getFullArticle(String disposition, int pos) {
        List<FullArticle> a = getModel().getArticles().get(disposition);
        if (a != null && a.size() > pos && pos >= 0) {
            return a.get(pos);
        }
        return null;
    }

    public ArticlesItem getArticle(String disposition, int pos) {
        FullArticle a = getFullArticle(disposition, pos);
        if (a != null) {
            return a.getArticlesItem();
        }
        return null;
    }

    @Override
    public List<ContentText> getContentTextList(String id) {
        List list = getModel().getArticles().get(id);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        return list;
    }

    public void setSelectedContentTextById(int id) {
        getModel().setCurrent((FullArticle) articles.findFullArticle(id));
        FullArticle c = getModel().getCurrent();
        if(c!=null && c.getArticlesItem().getDisposition()!=null){
            loadContentTexts(c.getArticlesItem().getDisposition().getName());
        }
    }

    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }

    @Override
    public CmsTextDisposition getContentDispositionByName(String name) {
        return ArticlesPlugin.get().findArticleDisposition(name);
    }

    @Override
    public String getContentDispositionName() {
        return getModel().getDisposition();
    }

    @Override
    public CmsTextDisposition getContentDisposition() {
        return getContentDispositionByName(getContentDispositionName());
    }

    @Override
    public ContentText getSelectedContentText() {
        return getModel().getCurrent();
    }
}
