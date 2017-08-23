/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "contact.fullName")
@Path("Contact")
@Properties(
        {
                @Property(name = "cache.navigationDepth", type = "int", value = "5"),
                @Property(name = "ui.auto-filter.department", value = "{expr='department',order=1}"),
                @Property(name = "ui.auto-filter.officialDiscipline", value = "{expr='officialDiscipline',order=2}"),
                @Property(name = "ui.auto-filter.situation", value = "{expr='situation',order=3}"),
                @Property(name = "ui.main-photo-provider", value = "net.vpc.app.vainruling.plugins.academic.web.photo.AcademicTeacherMainPhotoProvider"),
                @Property(name = UIConstants.ENTITY_TEXT_SEARCH_FACTORY, value = "net.vpc.app.vainruling.plugins.academic.service.util.AcademicTeacherObjSearchFactory")
        }
)
public class AcademicTeacher {

    @Id
    @Sequence

    private int id;
    @Main
    private AppContact contact;
    private AppUser user;
    private String discipline;
    private AcademicOfficialDiscipline officialDiscipline;
    @Summary
    @Field(
            updateAccessLevel = AccessLevel.PROTECTED,
            readAccessLevel = AccessLevel.PROTECTED
    )
    private AcademicTeacherDegree degree;

    @Summary
    @Field(
            updateAccessLevel = AccessLevel.PROTECTED,
            readAccessLevel = AccessLevel.PROTECTED
    )
    private AcademicTeacherSituation situation;
    @Summary
    private AppDepartment department;

//    @Summary
//    @Property(name = UIConstants.Grid.COLUMN_STYLE, value = "width:40px")
//    private boolean enabled = true;
    private AppPeriod startPeriod;
    private AppPeriod lastPeriod;
    private String uniqueCode;
    private String otherNames;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String publicObservations;
    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String privateObservations;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    @Field(
            defaultValue = "false",
            updateAccessLevel = AccessLevel.PROTECTED,
            readAccessLevel = AccessLevel.PROTECTED
    )
    private boolean deleted;
    @Field(
            updateAccessLevel = AccessLevel.PROTECTED,
            readAccessLevel = AccessLevel.PROTECTED
    )
    private String deletedBy;
    @Field(
            updateAccessLevel = AccessLevel.PROTECTED,
            readAccessLevel = AccessLevel.PROTECTED
    )
    private Timestamp deletedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppContact getContact() {
        return contact;
    }

    public void setContact(AppContact contact) {
        this.contact = contact;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public AcademicTeacherDegree getDegree() {
        return degree;
    }

    public void setDegree(AcademicTeacherDegree degree) {
        this.degree = degree;
    }

    public AcademicTeacherSituation getSituation() {
        return situation;
    }

    public void setSituation(AcademicTeacherSituation situation) {
        this.situation = situation;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }

    public AppPeriod getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(AppPeriod startPeriod) {
        this.startPeriod = startPeriod;
    }

    public AppPeriod getLastPeriod() {
        return lastPeriod;
    }

    public void setLastPeriod(AppPeriod lastPeriod) {
        this.lastPeriod = lastPeriod;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Timestamp deletedOn) {
        this.deletedOn = deletedOn;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public String toString() {
        if (contact != null) {
            return contact.toString();
        }
        return "AcademicTeacher{" + "id=" + id + ", contact=" + contact + '}';
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public String getPublicObservations() {
        return publicObservations;
    }

    public void setPublicObservations(String publicObservations) {
        this.publicObservations = publicObservations;
    }

    public String getPrivateObservations() {
        return privateObservations;
    }

    public void setPrivateObservations(String privateObservations) {
        this.privateObservations = privateObservations;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public AcademicOfficialDiscipline getOfficialDiscipline() {
        return officialDiscipline;
    }

    public void setOfficialDiscipline(AcademicOfficialDiscipline officialDiscipline) {
        this.officialDiscipline = officialDiscipline;
    }

    public String resolveFullName(){
        AppContact c = getContact();
        return c==null?String.valueOf(getId()): c.getFullName();
    }

    public String resolveFullTitle(){
        AppContact c = getContact();
        return c==null?String.valueOf(getId()): c.getFullTitle();
    }
}
