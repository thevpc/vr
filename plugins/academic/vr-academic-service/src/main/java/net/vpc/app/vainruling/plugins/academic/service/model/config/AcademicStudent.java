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
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicBac;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicClass;
import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicPreClass;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author vpc
 */
@Entity(listOrder = "contact.fullName")
@Path("Contact")
public class AcademicStudent {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.MAIN)
    private AppContact contact;
    private String subscriptionNumber;
    private AppDepartment department;
    private AppUser user;
    private AppPeriod firstSubscription;
    private AppPeriod lastSubscription;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicClass lastClass1;
    private AcademicClass lastClass2;
    private AcademicClass lastClass3;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AcademicStudentStage stage = AcademicStudentStage.ATTENDING;

    @Field(max = "4000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    private String publicObservations;
    @Field(max = "4000")
    @Property(name = UIConstants.FIELD_FORM_CONTROL, value = "textarea")
    private String privateObservations;

    private AcademicPreClass preClass;
    private int preClassRank;
    private int preClassRankMax;
    private AcademicBac baccalaureateClass;
    private double baccalaureateScore;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    @Field(defaultValue = "false")
    private boolean deleted;
    private String deletedBy;
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

    public AppPeriod getFirstSubscription() {
        return firstSubscription;
    }

    public void setFirstSubscription(AppPeriod firstSubscription) {
        this.firstSubscription = firstSubscription;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public AcademicClass getLastClass1() {
        return lastClass1;
    }

    public void setLastClass1(AcademicClass lastLevel1) {
        this.lastClass1 = lastLevel1;
    }

    public AcademicClass getLastClass2() {
        return lastClass2;
    }

    public void setLastClass2(AcademicClass lastLevel2) {
        this.lastClass2 = lastLevel2;
    }

    public AcademicClass getLastClass3() {
        return lastClass3;
    }

    public void setLastClass3(AcademicClass lastLevel3) {
        this.lastClass3 = lastLevel3;
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

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public String getSubscriptionNumber() {
        return subscriptionNumber;
    }

    public void setSubscriptionNumber(String subscriptionNumber) {
        this.subscriptionNumber = subscriptionNumber;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.id;
        hash = 41 * hash + Objects.hashCode(this.contact);
        hash = 41 * hash + Objects.hashCode(this.subscriptionNumber);
        hash = 41 * hash + Objects.hashCode(this.department);
        hash = 41 * hash + Objects.hashCode(this.firstSubscription);
        hash = 41 * hash + Objects.hashCode(this.lastClass1);
        hash = 41 * hash + Objects.hashCode(this.lastClass2);
        hash = 41 * hash + Objects.hashCode(this.lastClass3);
        hash = 41 * hash + Objects.hashCode(this.user);
        hash = 41 * hash + (this.deleted ? 1 : 0);
        hash = 41 * hash + Objects.hashCode(this.deletedBy);
        hash = 41 * hash + Objects.hashCode(this.deletedOn);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AcademicStudent other = (AcademicStudent) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.contact, other.contact)) {
            return false;
        }
        if (!Objects.equals(this.subscriptionNumber, other.subscriptionNumber)) {
            return false;
        }
        if (!Objects.equals(this.department, other.department)) {
            return false;
        }
        if (!Objects.equals(this.firstSubscription, other.firstSubscription)) {
            return false;
        }
        if (!Objects.equals(this.lastClass1, other.lastClass1)) {
            return false;
        }
        if (!Objects.equals(this.lastClass2, other.lastClass2)) {
            return false;
        }
        if (!Objects.equals(this.lastClass3, other.lastClass3)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.deletedBy, other.deletedBy)) {
            return false;
        }
        if (!Objects.equals(this.deletedOn, other.deletedOn)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (contact != null) {
            return contact.toString();
        }
        return "AcademicStudent{" + "id=" + id + ", contact=" + contact + ", subscriptionNumber=" + subscriptionNumber + ", department=" + department + ", firstSubscription=" + firstSubscription + ", lastClass1=" + lastClass1 + ", lastClass2=" + lastClass2 + ", lastClass3=" + lastClass3 + ", user=" + user + ", deleted=" + deleted + ", deletedBy=" + deletedBy + ", deletedOn=" + deletedOn + '}';
    }

    public AcademicStudentStage getStage() {
        return stage;
    }

    public void setStage(AcademicStudentStage stage) {
        this.stage = stage;
    }

    public AppPeriod getLastSubscription() {
        return lastSubscription;
    }

    public void setLastSubscription(AppPeriod lastSubscription) {
        this.lastSubscription = lastSubscription;
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

    public AcademicPreClass getPreClass() {
        return preClass;
    }

    public void setPreClass(AcademicPreClass preClass) {
        this.preClass = preClass;
    }

    public int getPreClassRank() {
        return preClassRank;
    }

    public void setPreClassRank(int preClassRank) {
        this.preClassRank = preClassRank;
    }

    public AcademicBac getBaccalaureateClass() {
        return baccalaureateClass;
    }

    public void setBaccalaureateClass(AcademicBac baccalaureateClass) {
        this.baccalaureateClass = baccalaureateClass;
    }

    public double getBaccalaureateScore() {
        return baccalaureateScore;
    }

    public void setBaccalaureateScore(double baccalaureateScore) {
        this.baccalaureateScore = baccalaureateScore;
    }

    public int getPreClassRankMax() {
        return preClassRankMax;
    }

    public void setPreClassRankMax(int preClassRankMax) {
        this.preClassRankMax = preClassRankMax;
    }

}
