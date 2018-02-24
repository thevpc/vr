/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import net.vpc.common.strings.StringUtils;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Config")
public class AcademicTeacherSituation {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    @Unique
    private String name;
    private String name2;
    private String name3;
    @Summary
    private AcademicTeacherSituationType type;

    public AcademicTeacherSituation() {
    }

    public AcademicTeacherSituation(int id, String name) {
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
        return StringUtils.nonNull(name);
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public AcademicTeacherSituationType getType() {
        return type;
    }

    public void setType(AcademicTeacherSituationType type) {
        this.type = type;
    }
}
