/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.model.config;

import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicTeacherDegree;
import net.vpc.upa.FormulaType;
import net.vpc.upa.ProtectionLevel;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.user.fullName")
@Path("Contact")
@Properties(
        {
                @Property(name = "cache.navigationDepth", valueType = "int", value = "5"),
                @Property(name = "ui.auto-filter.department", value = "{expr='this.user.department',order=1}"),
                @Property(name = "ui.auto-filter.officialDiscipline", value = "{expr='this.officialDiscipline',order=2}"),
                @Property(name = "ui.auto-filter.degree", value = "{expr='this.degree',order=3}"),
                @Property(name = "ui.auto-filter.situation", value = "{expr='this.situation',order=3}"),
                @Property(name = "ui.main-photo-provider", value = "net.vpc.app.vainruling.plugins.academic.service.obj.AcademicTeacherMainPhotoProvider"),
                @Property(name = UIConstants.ENTITY_TEXT_SEARCH_FACTORY, value = "net.vpc.app.vainruling.plugins.academic.service.util.AcademicTeacherObjSearchFactory")
        }
)
public class AcademicTeacher {

    @Path("Main")
    @Id
    @Sequence

    private int id;
    @Main
    private AppUser user;
    private String discipline;
    private AcademicOfficialDiscipline officialDiscipline;
    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private AcademicTeacherDegree degree;

    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private AcademicTeacherSituation situation;

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

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)

    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})

    private Timestamp updateDate;

    @Field(
            defaultValue = "false",
            protectionLevel = ProtectionLevel.PROTECTED)

    private boolean deleted;
    @Field(protectionLevel = ProtectionLevel.PROTECTED)

    private String deletedBy;
    @Field(protectionLevel = ProtectionLevel.PROTECTED)

    private Timestamp deletedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        if (user != null) {
            return user.toString();
        }
        return "AcademicTeacher{" + "id=" + id + ", user=" + user + '}';
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

//    public AppContact resolveContact(){
//        AppUser c = getUser();
//        return c==null?null: c.getContact();
//    }

    public String resolveFullName(){
        AppUser c = getUser();
        String n=null;
        if(c!=null){
            n=c.getFullName();
        }
        return n==null?String.valueOf(getId()): n;
    }

    public String resolveFullTitle(){
        AppUser c = getUser();
        return c==null?String.valueOf(getId()): c.getFullTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcademicTeacher that = (AcademicTeacher) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
