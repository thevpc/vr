/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Social")
public class AppArticleProperty {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Summary
    private AppArticle article;
    @Main
    private String name;

    @Properties({
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Summary
    @Field(max = "1024")
    private String value;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppArticle getArticle() {
        return article;
    }

    public void setArticle(AppArticle article) {
        this.article = article;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
