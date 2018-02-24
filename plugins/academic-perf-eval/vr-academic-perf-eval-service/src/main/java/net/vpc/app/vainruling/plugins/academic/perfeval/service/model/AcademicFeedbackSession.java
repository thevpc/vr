package net.vpc.app.vainruling.plugins.academic.perfeval.service.model;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicSemester;
import net.vpc.upa.config.*;

/**
 * Created by vpc on 6/26/17.
 */
@Entity(listOrder = "this.name desc")
@Path("/Education/Evaluation")
public class AcademicFeedbackSession {
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    private AppPeriod period;
//    private AppDepartment department;
    private AcademicSemester semester;

    @Summary
    private boolean read;
    @Summary
    private boolean write;

    public AcademicFeedbackSession() {
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

    public AppPeriod getPeriod() {
        return period;
    }

    public void setPeriod(AppPeriod period) {
        this.period = period;
    }

    public AcademicSemester getSemester() {
        return semester;
    }

    public void setSemester(AcademicSemester semester) {
        this.semester = semester;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

//    public AppDepartment getDepartment() {
//        return department;
//    }
//
//    public void setDepartment(AppDepartment department) {
//        this.department = department;
//    }
}
