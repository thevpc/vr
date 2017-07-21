package net.vpc.app.vr.plugins.academicprofile.service.model;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.config.*;
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
    @ManyToOne(type = RelationshipType.COMPOSITION)
    private AcademicTeacherCV teacherCV;
    private AcademicCVSection section;
    private String title;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String details;
    private Date fromDate;
    private Date toDate;
    private AppCompany company;
    @Field(max = "1024")
    private String keywords;

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
