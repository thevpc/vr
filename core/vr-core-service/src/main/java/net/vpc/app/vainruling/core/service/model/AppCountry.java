/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Admin/Config")
public class AppCountry {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN, UserFieldModifier.UNIQUE})
    private String name;
    private String name2;
    private String name3;

    public AppCountry() {
    }

    public AppCountry(int id, String name) {
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

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

}
