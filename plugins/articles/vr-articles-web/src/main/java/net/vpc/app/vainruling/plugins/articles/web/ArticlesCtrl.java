/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web;

import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.model.AppUser;
import net.vpc.app.vainruling.api.security.UserSession;
import net.vpc.app.vainruling.plugins.articles.service.ArticlesPlugin;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesFile;
import net.vpc.app.vainruling.plugins.articles.service.model.FullArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 *
 * @author vpc
 */
@Controller
@ManagedBean
@Scope(value = "session")
public class ArticlesCtrl {

    @Autowired
    private ArticlesPlugin articles;
    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    public void refesh() {
    }

    public static class Model {

        private FullArticle current;

        public FullArticle getCurrent() {
            return current;
        }

        public void setCurrent(FullArticle current) {
            this.current = current;
        }

    }

    public List<FullArticle> getWelcomeArticles() {
        return findArticles("Welcome");
    }

    public List<FullArticle> getMainRow1Articles() {
        return findArticles("Main.Row1");
    }

    public List<FullArticle> getMainRow2Articles() {
        return findArticles("Main.Row2");
    }

    public List<FullArticle> getMainRow3Articles() {
        return findArticles("Main.Row3");
    }

    public List<FullArticle> getActivities() {
        return findArticles("Activities");
    }

    public List<FullArticle> getNews() {
        return findArticles("News");
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
        AppUser u = VrApp.getBean(UserSession.class).getUser();
        return articles.findFullArticlesByUserAndCategory(u == null ? null : u.getLogin(), disposition);
    }

    public List<ArticlesFile> findArticlesFiles(int articleId) {
        return articles.findArticlesFiles(articleId);
    }
}
