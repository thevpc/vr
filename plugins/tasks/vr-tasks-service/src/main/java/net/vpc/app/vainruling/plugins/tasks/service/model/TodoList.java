/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Todo")
public class TodoList {

    public static final String LABO_ACTION = "sys-labo-action";
    public static final String LABO_TICKET = "sys-labo-ticket";
    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN, UserFieldModifier.UNIQUE})
    private String name;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    @Properties(@Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.TEXTAREA))
    private String description;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private boolean systemList;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppUser respUser;
    @Field(modifiers = {UserFieldModifier.SUMMARY})
    private AppProfile respProfile;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSystemList() {
        return systemList;
    }

    public void setSystemList(boolean systemList) {
        this.systemList = systemList;
    }

    public AppUser getRespUser() {
        return respUser;
    }

    public void setRespUser(AppUser respUser) {
        this.respUser = respUser;
    }

    public AppProfile getRespProfile() {
        return respProfile;
    }

    public void setRespProfile(AppProfile ownerProfile) {
        this.respProfile = ownerProfile;
    }

}
