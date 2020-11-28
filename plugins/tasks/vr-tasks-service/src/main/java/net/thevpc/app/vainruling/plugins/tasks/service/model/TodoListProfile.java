/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.tasks.service.model;

import net.thevpc.app.vainruling.core.service.model.AppProfile;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Repository/Todo")
public class TodoListProfile {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    private TodoList list;
    @Summary
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
