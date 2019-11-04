/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model.content;

import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.index, this.title")
@Path("/Repository/Social")
public class AppArticleDispositionBundle {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    @Unique
    private String name;
    @Main
    private String title;
    @Summary
    private String mainColor;

    @Summary
    private String mainIconStyle;

    @Summary
    private int index;

    @Summary
    @Field(max = "1024")
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainColor() {
        return mainColor;
    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public String getMainIconStyle() {
        return mainIconStyle;
    }

    public void setMainIconStyle(String mainIconStyle) {
        this.mainIconStyle = mainIconStyle;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
