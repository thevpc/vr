/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.FormulaType;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

import java.sql.Timestamp;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("/Contact")
public class AppProfile {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    private String code;
    @Main
    @Unique
    private String name;
    private String name2;
    private String name3;
//    @Summary
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String shortDescription;
    @Summary
    @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
    private String inherited;
    /**
     * admin user(s)
     */
    @Summary
    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION),
                @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String admin;

    /**
     * custom profiles type depends on creator plugin
     */
    private String customType;

    /**
     * custom profiles are managed by application
     */
    @Field(defaultValue = "false")
    private boolean custom;
    /**
     * custom profiles are managed by application
     */
    @Field(defaultValue = "true")
    private boolean shareFolder;
    /**
     * if true, this is considered as a composer group. A composer group is NOT
     * a top level group and hence should not be assigned directly to users. It
     * is meant to be part (parent) of top level groups to help composing more
     */
    private boolean composer;

    @Field(max = "4000")
    @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA)
    private String description;

    @Path("Trace")
//    @Properties(
//            @Property(name = UIConstants.Form.SEPARATOR, value = "Trace"))
    @Formula(value = "CurrentTimestamp()", formulaType = FormulaType.PERSIST)
    @Field(excludeModifiers = UserFieldModifier.UPDATE)
    private Timestamp creationDate;
    @Formula(value = "CurrentTimestamp()", formulaType = {FormulaType.PERSIST, FormulaType.UPDATE})
    private Timestamp updateDate;

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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
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
        return String.valueOf(name);
    }

//    public static String getCodeOrName(AppProfile u) {
//        if (u == null) {
//            return null;
//        }
//        if (u.getCode()!=null && u.getCode().length()>0) {
//            return u.getCode();
//        }
//        if (u.getName()!=null && u.getName().length()>0) {
//            return u.getName();
//        }
//        return null;
//    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AppProfile that = (AppProfile) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getInherited() {
        return inherited;
    }

    public void setInherited(String inherited) {
        this.inherited = inherited;
    }

    public boolean isComposer() {
        return composer;
    }

    public void setComposer(boolean composer) {
        this.composer = composer;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isShareFolder() {
        return shareFolder;
    }

    public void setShareFolder(boolean shareFolder) {
        this.shareFolder = shareFolder;
    }

}
