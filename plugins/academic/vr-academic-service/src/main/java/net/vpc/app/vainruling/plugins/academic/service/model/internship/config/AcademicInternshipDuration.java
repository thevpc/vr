/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.internship.config;

import net.vpc.app.vainruling.core.service.util.DurationType;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Education/Config")
public class AcademicInternshipDuration {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    private int count;
    private DurationType durationType;

    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicInternshipType internshipType;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public AcademicInternshipType getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(AcademicInternshipType internshipType) {
        this.internshipType = internshipType;
    }

}
