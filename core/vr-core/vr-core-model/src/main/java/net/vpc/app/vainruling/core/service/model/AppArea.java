/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Admin/Config")
public class AppArea {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    @Unique
    private String name;
    @Summary
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    @Summary
    @Hierarchy
    private AppArea parent;

    @Summary
    private AppAreaType type;

    public AppArea() {
    }

    public AppArea(String name, String description, AppAreaType type, AppArea parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.type = type;
    }

    public AppAreaType getType() {
        return type;
    }

    public void setType(AppAreaType type) {
        this.type = type;
    }

    public AppArea getParent() {
        return parent;
    }

    public void setParent(AppArea parent) {
        this.parent = parent;
    }

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
        if (parent != null) {
            return parent.toString() + "/" + String.valueOf(name);
        }
        return String.valueOf(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppArea that = (AppArea) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
