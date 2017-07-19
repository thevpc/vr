package net.vpc.app.vainruling.plugins.academic.service.model.current;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.types.Date;

/**
 * Created by vpc on 7/19/17.
 */
@Entity
@Path("Education")
public class AcademicTeacherCVItem {

    @Id
    @Sequence
    private int id;
    private AcademicTeacherCV teacherCV;
    private String title;
    private String details;
    private Date fromDate;
    private Date toDate;
    private AppCompany company;
    private String keywords;
    private AcademicCVSection section;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicTeacherCV getTeacherCV() {
        return teacherCV;
    }

    public void setTeacherCV(AcademicTeacherCV teacherCV) {
        this.teacherCV = teacherCV;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public AppCompany getCompany() {
        return company;
    }

    public void setCompany(AppCompany company) {
        this.company = company;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public AcademicCVSection getSection() {
        return section;
    }

    public void setSection(AcademicCVSection section) {
        this.section = section;
    }
}
