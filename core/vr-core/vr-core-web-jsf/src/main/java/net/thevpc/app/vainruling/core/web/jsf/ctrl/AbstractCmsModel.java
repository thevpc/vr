/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.thevpc.app.vainruling.core.service.content.VrContentText;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractCmsModel {


    private VrContentText current;
    private String disposition;

    private Map<String, List<VrContentText>> articles = new HashMap<>();

    public VrContentText getCurrent() {
        return current;
    }

    public void setCurrent(VrContentText current) {
        this.current = current;
    }

    public Map<String, List<VrContentText>> getArticles() {
        return articles;
    }


    public void setArticles(Map<String, List<VrContentText>> articles) {
        this.articles = articles;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }
}
