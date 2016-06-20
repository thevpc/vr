/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web;

import net.vpc.app.vainruling.core.service.UpaAware;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSession;
import net.vpc.app.vainruling.plugins.articles.service.ArticlesPlugin;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesFile;
import net.vpc.app.vainruling.plugins.articles.service.model.FullArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vpc
 */
@Controller
@ManagedBean
@Scope(value = "session")
public class ArticlesCtrl {

    @Autowired
    private ArticlesPlugin articles;
    private Model model = new Model();
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

    public Model getModel() {
        return model;
    }

    public void refesh() {
    }

    public void loadArticles(String name) {
        getModel().getArticles().put(name, findArticles(name));
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

    @UpaAware
    public List<FullArticle> findArticles(String disposition) {
        AppUser u = VrApp.getBean(UserSession.class).getUser();
        return articles.findFullArticlesByUserAndCategory(u == null ? null : u.getLogin(), disposition);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return articles.findArticlesFiles(articleId);
    }

    public static class Model {

        private FullArticle current;
        private Map<String, List<FullArticle>> articles = new HashMap<>();

        public FullArticle getCurrent() {
            return current;
        }

        public void setCurrent(FullArticle current) {
            this.current = current;
        }

        public Map<String, List<FullArticle>> getArticles() {
            return articles;
        }

        public void setArticles(Map<String, List<FullArticle>> articles) {
            this.articles = articles;
        }
    }
}
