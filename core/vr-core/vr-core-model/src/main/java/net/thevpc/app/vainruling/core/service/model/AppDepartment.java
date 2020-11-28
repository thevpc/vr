/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("/Repository/General")
public class AppDepartment {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Unique
    @Summary
    private String code;
    @Main
    private String name;
    private String name2;
    private String name3;
    @Hierarchy
    private AppDepartment parent;
    @Summary
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    public AppDepartment getParent() {
        return parent;
    }

    public void setParent(AppDepartment parent) {
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    @Override
    public String toString() {
        return code + ":" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDepartment that = (AppDepartment) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
