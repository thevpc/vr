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
public class AcademicStudentCVItem {

    @Id
    @Sequence
    private int id;
    @ManyToOne(type = RelationshipType.COMPOSITION)
    @Summary
    private AcademicStudentCV studentCV;
    @Summary
    private AcademicCVSection section;
    @Summary
    private String title;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String details;
    @Summary
    private Date fromDate;
    @Summary
    private Date toDate;
    @Summary
    private AppCompany company;
    @Field(max = "1024")
    private String keywords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AcademicStudentCV getStudentCV() {
        return studentCV;
    }

    public void setStudentCV(AcademicStudentCV studentCV) {
        this.studentCV = studentCV;
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
