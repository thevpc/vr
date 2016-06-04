/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import java.util.List;
import net.vpc.upa.Entity;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.filters.Fields;

/**
 *
 * @author vpc
 */
public class ObjFieldSelection {

    private String name;
    private Entity entity;

    public ObjFieldSelection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void prepare(Entity entity) {
        this.entity = entity;
    }

    public List<net.vpc.upa.Field> getVisibleFields() {
        return getEntity().getFields(Fields.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY));
    }

}
