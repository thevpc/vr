/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.FormulaType;
import net.vpc.upa.PasswordStrategyType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;
import net.vpc.upa.types.DateTime;

import java.sql.Timestamp;

/**
 * @author vpc
 */
@Entity(listOrder = "contact.fullName")
@Path("Contact")
public class AppUser {

    @Id
    @Sequence
    private int id;

    @Field(modifiers = {UserFieldModifier.UNIQUE, UserFieldModifier.SUMMARY})
    private String login;

    @Password(strategyType = PasswordStrategyType.MD5)
    private String password;

    @Field(modifiers = {UserFieldModifier.MAIN})
    private AppContact contact;
    @Summary
    private AppDepartment department;
    @Summary
    private AppUserType type;

    @Properties(
            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", type = FormulaType.PERSIST)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", type = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

    @Field(persistAccessLevel = AccessLevel.PRIVATE, updateAccessLevel = AccessLevel.PRIVATE, readAccessLevel = AccessLevel.PROTECTED, modifiers = UserFieldModifier.SUMMARY
    )
    private DateTime lastConnexionDate;

    @Field(defaultValue = "0", persistAccessLevel = AccessLevel.PRIVATE, updateAccessLevel = AccessLevel.PROTECTED, readAccessLevel = AccessLevel.PROTECTED, modifiers = UserFieldModifier.SUMMARY
    )
    private long connexionCount;

    @Field(defaultValue = "true", persistAccessLevel = AccessLevel.PRIVATE, updateAccessLevel = AccessLevel.PROTECTED, readAccessLevel = AccessLevel.PROTECTED, modifiers = UserFieldModifier.SUMMARY
    )
    private boolean enabled;

    private boolean deleted;

    private String deletedBy;

    private Timestamp deletedOn;

    @Field(defaultValue = "false", persistAccessLevel = AccessLevel.PROTECTED, updateAccessLevel = AccessLevel.PROTECTED, readAccessLevel = AccessLevel.PROTECTED, modifiers = UserFieldModifier.SUMMARY
    )
    private boolean welcomeSent;

    /**
     * password field generated automatically, should be reset on some event
     * though
     */
    @Field(persistAccessLevel = AccessLevel.PROTECTED, updateAccessLevel = AccessLevel.PROTECTED, readAccessLevel = AccessLevel.PROTECTED)
    private String passwordAuto;

    public static String getName(AppUser t) {
        if (t.getContact() == null) {
            String log = t.getLogin();
            if (StringUtils.isEmpty(log)) {
                return "Sans Nom";
            }
            return log;
        }
        return AppContact.getName(t.getContact());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AppDepartment getDepartment() {
        return department;
    }

    public void setDepartment(AppDepartment department) {
        this.department = department;
    }

    public AppUserType getType() {
        return type;
    }

    public void setType(AppUserType type) {
        this.type = type;
    }

    public DateTime getLastConnexionDate() {
        return lastConnexionDate;
    }

    public void setLastConnexionDate(DateTime lastConnexionDate) {
        this.lastConnexionDate = lastConnexionDate;
    }

    public long getConnexionCount() {
        return connexionCount;
    }

    public void setConnexionCount(long connexionCount) {
        this.connexionCount = connexionCount;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public AppContact getContact() {
        return contact;
    }

    public void setContact(AppContact contact) {
        this.contact = contact;
    }

    public String getPasswordAuto() {
        return passwordAuto;
    }

    public void setPasswordAuto(String passwordAuto) {
        this.passwordAuto = passwordAuto;
    }

    public boolean isWelcomeSent() {
        return welcomeSent;
    }

    public void setWelcomeSent(boolean welcomeSent) {
        this.welcomeSent = welcomeSent;
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

    @Override
    public String toString() {
        return String.valueOf(login);
    }
}
