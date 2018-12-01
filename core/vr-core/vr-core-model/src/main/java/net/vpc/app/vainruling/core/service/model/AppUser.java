/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.*;
import net.vpc.upa.config.*;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;
import net.vpc.upa.types.DateTime;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.fullName")
@Path("Contact")
@Properties(
        {
            @Property(name = "ui.auto-filter.department", value = "{expr='this.department',order=1}")
            ,
                @Property(name = "ui.auto-filter.type", value = "{expr='this.type',order=2}")
            ,
                @Property(name = "ui.auto-filter.company", value = "{expr='this.company',order=3}")
            ,
                @Property(name = "ui.auto-filter.positionTitle1", value = "{expr='this.positionTitle1',order=4}")
            ,
                @Property(name = "ui.auto-filter.company", value = "{expr='this.company',order=5}")
            ,
                @Property(name = "ui.main-photo-provider", value = "net.vpc.app.vainruling.core.service.obj.AppUserMainPhotoProvider")
            ,
                @Property(name = UIConstants.ENTITY_TEXT_SEARCH_FACTORY, value = "net.vpc.app.vainruling.core.service.obj.AppUserObjSearchFactory")
        })
public class AppUser extends AppPersonContactBase implements Cloneable {

    @Path(value = "UserInfos", position = 0)
    @Summary
    @Unique
    @Field(
            updateProtectionLevel = ProtectionLevel.PROTECTED
    )

    private String login;

    @Password(strategyType = PasswordStrategyType.MD5)
    @Field(
            updateProtectionLevel = ProtectionLevel.PROTECTED,
            readProtectionLevel = ProtectionLevel.PROTECTED
    )

    private String password;

    @Summary
    private AppDepartment department;

    @Summary
    private AppUserType type;

    @Path("Trace")
    @Summary
    @Field(protectionLevel = ProtectionLevel.PRIVATE)
    private DateTime lastConnexionDate;

    @Summary
    @Field(protectionLevel = ProtectionLevel.PRIVATE)
    private long connexionCount;

    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private boolean welcomeSent;

    /**
     * password field generated automatically, should be reset on some event
     * though
     */
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
    private String passwordAuto;

    private int lastLoginFailures;

    public void copyFrom(AppContactBase other) {
        super.copyFrom(other);
        if (other instanceof AppUser) {
            AppUser u = (AppUser) other;
            this.login = u.login;
            this.password = u.password;
            this.department = u.department;
            this.type = u.type;
            this.lastConnexionDate = u.lastConnexionDate;
            this.connexionCount = u.connexionCount;
            this.welcomeSent = u.welcomeSent;
            this.passwordAuto = u.passwordAuto;
        }
    }

    public static String getName(AppUser t) {
        String log = t.getLogin();
        if (log == null) {
            return "Sans Nom";
        }
        return log;
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

    @Override
    public String toString() {
        return String.valueOf(login);
    }

//    public String resolveFullName() {
//        return getFullName();
//    }
//
//    public String resolveFirstName() {
//        AppContact c = getContact();
//        return c == null ? String.valueOf(getId()) : c.getFirstName();
//    }
//
//    public String resolveLastName() {
//        AppContact c = getContact();
//        return c == null ? String.valueOf(getId()) : c.getLastName();
//    }
//
//    public String resolveFullTitle() {
//        AppContact c = getContact();
//        return c == null ? String.valueOf(getId()) : c.getFullTitle();
//    }
    public AppUser copy() {
        AppUser appUser = null;
        try {
            appUser = (AppUser) clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Unexpected");
        }
        return appUser;
    }

    public int getLastLoginFailures() {
        return lastLoginFailures;
    }

    public void setLastLoginFailures(int lastLoginFailures) {
        this.lastLoginFailures = lastLoginFailures;
    }

}
