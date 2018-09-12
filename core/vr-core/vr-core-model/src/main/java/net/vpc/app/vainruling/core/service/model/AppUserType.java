/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Admin/Security")
public class AppUserType {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    private String code;
    @Main
    private String name;
    @Summary
    private String name2;
    @Summary
    private String name3;

    public AppUserType() {
    }

    public AppUserType(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public AppUserType(String code, String name) {
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public static String getCodeOrName(AppUserType u) {
        if (u == null) {
            return null;
        }
        if (u.getCode() != null && u.getCode().length() > 0) {
            return u.getCode();
        }
        if (u.getName() != null && u.getName().length() > 0) {
            return u.getName();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AppUserType that = (AppUserType) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
