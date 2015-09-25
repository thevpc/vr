/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.api.model.AppDepartment;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;

/**
 * filiere
 *
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicProgram {

    @Id
    @Sequence

    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String name;
    private String name2;


    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppDepartment department;

    public AcademicProgram() {
    }

    public AcademicProgram(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
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
        return StringUtils.nonnull(name);
    }

}
