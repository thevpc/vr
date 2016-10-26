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
public class ArticlesProperty {

    @Id
    @Sequence
    private int id;

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
