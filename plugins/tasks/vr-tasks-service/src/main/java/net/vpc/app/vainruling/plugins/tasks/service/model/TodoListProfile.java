/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.app.vainruling.core.service.model.AppProfile;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity
@Path("Todo/Config")
public class TodoListProfile {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private TodoList list;
    @Field(modifiers = UserFieldModifier.SUMMARY)
    private AppProfile profile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TodoList getList() {
        return list;
    }

    public void setList(TodoList list) {
        this.list = list;
    }

    public AppProfile getProfile() {
        return profile;
    }

    public void setProfile(AppProfile profile) {
        this.profile = profile;
    }

}
