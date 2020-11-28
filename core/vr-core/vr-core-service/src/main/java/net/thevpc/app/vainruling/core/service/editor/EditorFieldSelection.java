/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.upa.Entity;
import net.thevpc.upa.FieldModifier;
import net.thevpc.upa.filters.FieldFilters;

import java.util.List;
import net.thevpc.app.vainruling.VrAccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
public class EditorFieldSelection {

    private String name;
    private Entity entity;

    public EditorFieldSelection(String name) {
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

    public void prepare(Entity entity,VrAccessMode mode) {
        this.entity = entity;
    }

    public List<net.thevpc.upa.Field> getVisibleFields() {
        return getEntity().getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY));
    }

    public void load() {

    }

    public void save() {

    }

    public void reset() {
    }

}
