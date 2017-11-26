/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.app.vainruling.core.service.obj.AppFile;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Social")
public class ArticlesFile implements AppFile{

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private ArticlesItem article;

    @Main
    private String name;

    @Properties({
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.FILE),
            @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    }
    )
    @Summary
    private String path;


    private String style;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArticlesItem getArticle() {
        return article;
    }

    public void setArticle(ArticlesItem article) {
        this.article = article;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArticlesFile that = (ArticlesFile) o;

        if (id != that.id) return false;
        if (position != that.position) return false;
        if (article != null ? !article.equals(that.article) : that.article != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return style != null ? style.equals(that.style) : that.style == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (article != null ? article.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (style != null ? style.hashCode() : 0);
        result = 31 * result + position;
        return result;
    }
}
