/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.model.content.FullArticle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractCmsModel {


    private FullArticle current;
    private String disposition;

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

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }
}
