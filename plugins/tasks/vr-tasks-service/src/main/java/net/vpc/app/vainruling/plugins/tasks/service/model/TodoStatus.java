/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Repository/Todo")
public class TodoStatus {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Main
    private String name;
    @Summary
    @ToString
    private TodoStatusType type;

    @Summary
    private TodoStatusGroup statusGroup;

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

    public TodoStatusGroup getStatusGroup() {
        return statusGroup;
    }

    public void setStatusGroup(TodoStatusGroup statusGroup) {
        this.statusGroup = statusGroup;
    }

    public TodoStatusType getType() {
        return type;
    }

    public void setType(TodoStatusType type) {
        this.type = type;
    }

}
