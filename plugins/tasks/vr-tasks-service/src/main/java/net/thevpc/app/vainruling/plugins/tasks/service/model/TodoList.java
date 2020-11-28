/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.tasks.service.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Todo")
public class TodoList {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Unique
    private String name;
    @Main
    private String label;
    @Summary
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    private String description;
    @Summary
    private boolean systemList;
    @Summary
    private boolean sharableList;
    @Summary
    private AppUser respUser;


    @Summary
    private TodoStatusGroup statusGroup;

    @Summary
    @Properties(
            {
                @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.PROFILE_EXPRESSION),
                @Property(name = UIConstants.Form.SPAN, value = "MAX_VALUE")
            }
    )
    private String collaborators;

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

    public TodoStatusGroup getStatusGroup() {
        return statusGroup;
    }

    public void setStatusGroup(TodoStatusGroup statusGroup) {
        this.statusGroup = statusGroup;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(String collaborators) {
        this.collaborators = collaborators;
    }

    public boolean isSharableList() {
        return sharableList;
    }

    public void setSharableList(boolean sharableList) {
        this.sharableList = sharableList;
    }

}
