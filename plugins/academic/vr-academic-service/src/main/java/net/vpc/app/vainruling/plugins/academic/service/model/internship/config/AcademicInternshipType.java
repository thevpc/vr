/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.internship.config;

import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicInternshipType {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String name;

    @Field(defaultValue = "false")

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
}
