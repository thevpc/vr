/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.service.model;

import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.callbacks.EntityListener;
import net.vpc.upa.callbacks.EntityListenerAdapter;
import net.vpc.upa.callbacks.PersistenceUnitListener;
import net.vpc.upa.callbacks.PersistenceUnitListenerAdapter;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Sequence;
import net.vpc.upa.config.ToString;

/**
 *
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
    @Field(modifiers = UserFieldModifier.SUMMARY)
    @ToString
    private TodoStatusType type;
    @Field(modifiers = UserFieldModifier.SUMMARY)
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
