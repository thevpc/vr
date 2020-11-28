/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.report.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.config.*;

import java.util.Date;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education/Evaluation")
public class AcademicReportTitle {

    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Summary
    private AcademicTeacher teacher;
    @Summary
    private Date creationDate;
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String observations;

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

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


}
