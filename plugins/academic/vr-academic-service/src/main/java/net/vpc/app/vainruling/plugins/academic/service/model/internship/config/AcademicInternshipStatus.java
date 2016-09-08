/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.internship.config;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "name")
@Path("Education/Config")
public class AcademicInternshipStatus {

    @Id
    @Sequence

    private int id;
    @Main
    private String name;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicInternshipType internshipType;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "General")}
    )
    @Field(defaultValue = "false")
    private boolean studentUpdatesDescr;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Encadrants")}
    )

    @Field(defaultValue = "false")
    private boolean studentSeesSupervisors;

    @Field(defaultValue = "false")
    private boolean studentUpdatesSupervisors;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Rapports")}
    )

    @Field(defaultValue = "false")
    private boolean enabledReport1;

    @Field(defaultValue = "false")
    private boolean studentUpdatesReport1;

    @Field(defaultValue = "false")
    private boolean enabledReport2;

    @Field(defaultValue = "false")
    private boolean studentUpdatesReport2;

    @Field(defaultValue = "false")
    private boolean enabledReport3;

    @Field(defaultValue = "false")
    private boolean studentUpdatesReport3;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Evaluation")}
    )

    @Field(defaultValue = "false")
    private boolean studentSeesEvaluators;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Enseignants")}
    )
    @Field(defaultValue = "false")
    private boolean supervisorRequestable;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Comité")}
    )
    @Field(defaultValue = "false")
    private boolean boardUpdatesDescr;

    @Field(defaultValue = "false")
    private boolean boardUpdatesSupervisors;

    @Field(defaultValue = "false")
    private boolean boardUpdatesEvaluators;

    @Properties({
            @Property(name = UIConstants.Form.SEPARATOR, value = "Cloture")}
    )
    @Field(defaultValue = "false")
    private boolean closed;

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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isStudentUpdatesDescr() {
        return studentUpdatesDescr;
    }

    public void setStudentUpdatesDescr(boolean studentUpdatesDescr) {
        this.studentUpdatesDescr = studentUpdatesDescr;
    }

    public boolean isStudentUpdatesReport1() {
        return studentUpdatesReport1;
    }

    public void setStudentUpdatesReport1(boolean studentUpdatesReport1) {
        this.studentUpdatesReport1 = studentUpdatesReport1;
    }

    public boolean isStudentUpdatesReport2() {
        return studentUpdatesReport2;
    }

    public void setStudentUpdatesReport2(boolean studentUpdatesReport2) {
        this.studentUpdatesReport2 = studentUpdatesReport2;
    }

    public boolean isStudentUpdatesReport3() {
        return studentUpdatesReport3;
    }

    public void setStudentUpdatesReport3(boolean studentUpdatesReport3) {
        this.studentUpdatesReport3 = studentUpdatesReport3;
    }

    public boolean isStudentSeesEvaluators() {
        return studentSeesEvaluators;
    }

    public void setStudentSeesEvaluators(boolean studentSeesEvaluators) {
        this.studentSeesEvaluators = studentSeesEvaluators;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStudentUpdatesSupervisors() {
        return studentUpdatesSupervisors;
    }

    public void setStudentUpdatesSupervisors(boolean studentUpdatesSupervisors) {
        this.studentUpdatesSupervisors = studentUpdatesSupervisors;
    }

    public boolean isStudentSeesSupervisors() {
        return studentSeesSupervisors;
    }

    public void setStudentSeesSupervisors(boolean studentSeesSupervisors) {
        this.studentSeesSupervisors = studentSeesSupervisors;
    }

    public boolean isEnabledReport1() {
        return enabledReport1;
    }

    public void setEnabledReport1(boolean enabledReport1) {
        this.enabledReport1 = enabledReport1;
    }

    public boolean isEnabledReport2() {
        return enabledReport2;
    }

    public void setEnabledReport2(boolean enabledReport2) {
        this.enabledReport2 = enabledReport2;
    }

    public boolean isEnabledReport3() {
        return enabledReport3;
    }

    public void setEnabledReport3(boolean enabledReport3) {
        this.enabledReport3 = enabledReport3;
    }

    public AcademicInternshipType getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(AcademicInternshipType internshipType) {
        this.internshipType = internshipType;
    }

    public boolean isBoardUpdatesDescr() {
        return boardUpdatesDescr;
    }

    public void setBoardUpdatesDescr(boolean boardUpdatesDescr) {
        this.boardUpdatesDescr = boardUpdatesDescr;
    }

    public boolean isBoardUpdatesSupervisors() {
        return boardUpdatesSupervisors;
    }

    public void setBoardUpdatesSupervisors(boolean boardUpdatesSupervisors) {
        this.boardUpdatesSupervisors = boardUpdatesSupervisors;
    }

    public boolean isBoardUpdatesEvaluators() {
        return boardUpdatesEvaluators;
    }

    public void setBoardUpdatesEvaluators(boolean boardUpdatesEvaluators) {
        this.boardUpdatesEvaluators = boardUpdatesEvaluators;
    }

    public boolean isSupervisorRequestable() {
        return supervisorRequestable;
    }

    public void setSupervisorRequestable(boolean supervisorRequestable) {
        this.supervisorRequestable = supervisorRequestable;
    }

}
