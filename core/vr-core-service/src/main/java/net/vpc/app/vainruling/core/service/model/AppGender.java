/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.common.strings.StringUtils;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Admin/Config")
public class AppGender {

    @Id
    @Sequence

    private int id;
    //    @Unique
    private String code;
    @Main
    private String name;
    private String name2;
    private String name3;

    public AppGender() {
    }

    public AppGender(int id, String name) {
        this.id = id;
        this.name = name;
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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppGender appGender = (AppGender) o;

        if (id != appGender.id) return false;
        if (code != null ? !code.equals(appGender.code) : appGender.code != null) return false;
        if (name != null ? !name.equals(appGender.name) : appGender.name != null) return false;
        if (name2 != null ? !name2.equals(appGender.name2) : appGender.name2 != null) return false;
        return name3 != null ? name3.equals(appGender.name3) : appGender.name3 == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (name2 != null ? name2.hashCode() : 0);
        result = 31 * result + (name3 != null ? name3.hashCode() : 0);
        return result;
    }
}
