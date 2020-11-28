/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.model;

import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("/Repository/General")
public class AppIndustry {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    @Unique
    private String name;
    private String name2;

    public AppIndustry() {
    }

    public AppIndustry(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
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

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppIndustry that = (AppIndustry) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
