/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.web;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope(value = "session")
public class ArticlesModel {


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
