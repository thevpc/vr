/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import net.vpc.upa.AccessMode;
import net.vpc.upa.Entity;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.filters.FieldFilters;

import java.util.List;

/**
 * @author taha.bensalah@gmail.com
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

    public void prepare(Entity entity,AccessMode mode) {
        this.entity = entity;
    }

    public List<net.vpc.upa.Field> getVisibleFields() {
        return getEntity().getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY));
    }

}
