/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.*;

/**
 * @author vpc
 */
@Entity(listOrder = "name")
@Path("Todo/Config")
public class TodoStatus {

    @Id
    @Sequence
    private int id;
    @Field(modifiers = {UserFieldModifier.MAIN})
    private String name;
    @Summary
    @ToString
    private TodoStatusType type;
    @Summary
    private TodoList list;

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

    public TodoList getList() {
        return list;
    }

    public void setList(TodoList list) {
        this.list = list;
    }

    public TodoStatusType getType() {
        return type;
    }

    public void setType(TodoStatusType type) {
        this.type = type;
    }

}
