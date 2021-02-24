/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model.content;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.RelationshipType;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("/Repository/Social")
public class AppArticleDispositionBinding {

    @Id
    @Sequence
    @Main
    private int id;
    @Property(name = UIConstants.Form.COMPOSITION_LIST_FIELD, value = "disposition")
    @ManyToOne(relationType = RelationshipType.COMPOSITION)
    private AppArticle article;
    private AppArticleDisposition disposition;

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

    public AppArticleDisposition getDisposition() {
        return disposition;
    }

    public void setDisposition(AppArticleDisposition disposition) {
        this.disposition = disposition;
    }

}
