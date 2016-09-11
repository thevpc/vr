/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.CmsTextService;
import net.vpc.app.vainruling.core.service.content.ContentText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        getModel().getArticles().put(name, a);
        if (getModel().getCurrent() == null) {
            if (a != null && a.size() > 0) {
                getModel().setCurrent(a.get(0));
            } else {
                getModel().setCurrent(null);
            }
        }
    }

    public List<FullArticle> getMainRow1Articles() {
        return findArticles("Main.Row1");
    }

//    public List<FullArticle> getWelcomeArticles() {
//        getModel().getArticles().put("Welcome",findArticles("Welcome"))
//        return findArticles("Welcome");
//    }

    public List<FullArticle> getMainRow2Articles() {
        return findArticles("Main.Row2");
    }

    public List<FullArticle> getMainRow3Articles() {
        return findArticles("Main.Row3");
    }

    public List<FullArticle> getActivities() {
        return findArticles("Activities");
    }

    public String getImageSwitchRandomEffect() {
        return imageSwitchEffects[(int) (Math.random() * imageSwitchEffects.length)];
    }

    public List<FullArticle> getMainRow4Articles() {
        return findArticles("Main.Row4");
    }

    public List<FullArticle> getMainRow5Articles() {
        return findArticles("Main.Row5");
    }

    public List<FullArticle> getMainRow6Articles() {
        return findArticles("Main.Row6");
    }

    public List<FullArticle> getMainRow7Articles() {
        return findArticles("Main.Row7");
    }

    public List<FullArticle> findArticles(String disposition) {
        AppUser u = UserSession.getCurrentUser();
        return articles.findFullArticlesByUserAndCategory(u == null ? null : u.getLogin(), disposition);
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

    public void setSelectedContentText(int id) {
        getModel().setCurrent((FullArticle) articles.findFullArticle(id));
    }

    public List<ContentText> getContentTextListHead(String id, int max) {
        List<ContentText> list = getContentTextList(id);
        if (list.size() > max) {
            return list.subList(0, max);
        }
        return list;
    }
}
