/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.upa.*;
import net.vpc.upa.filters.FieldFilters;

import java.util.*;

/**
 * @author taha.bensalah@gmail.com
 */
public class ObjFieldFieldSelection extends ObjFieldSelection {

    private List<SelField> fields = new ArrayList<>();

    public ObjFieldFieldSelection() {
        super("list");
    }

    public ObjFieldFieldSelection(Entity entity, String[] fields,AccessMode mode) {
        super("list");
        updateFields(mode,entity, new HashSet<String>(Arrays.asList(fields)));
    }

    @Override
    public void prepare(Entity entity, AccessMode mode) {
        Entity old = getEntity();
        setEntity(entity);
        Set<String> oldSelection = new HashSet<>();
        if (old == null || !old.getName().equals(entity.getName())) {
            //do nothing!
            oldSelection=new HashSet<>();
            for (Field field : getEntity().getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY))) {
                oldSelection.add(field.getName());
            }
        } else {
            for (SelField field : fields) {
                if (field.isSelected()) {
                    oldSelection.add(field.getField().getName());
                }
            }
        }
        updateFields(mode,entity, oldSelection);
    }

    private void updateFields(AccessMode mode,Entity entity, Set<String> selectedFieldNames) {
        fields.clear();
        int pos = 0;
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        boolean admin = core.isCurrentSessionAdmin();
        I18n i18n = VrApp.getBean(I18n.class);
        for (Field field : entity.getFields()) {
            //should test on field visibility
            AccessLevel r = field.getReadAccessLevel();
            AccessLevel u = field.getUpdateAccessLevel();
            boolean show = false;
            switch (mode){
                case READ:{
                    show=field.getEffectiveReadAccessLevel()!=AccessLevel.INACCESSIBLE;
                    break;
                }
                case UPDATE:{
                    show=field.getEffectiveUpdateAccessLevel()!=AccessLevel.INACCESSIBLE;
                    break;
                }
                case PERSIST:{
                    show=field.getEffectivePersistAccessLevel()!=AccessLevel.INACCESSIBLE;
                    break;
                }
            }
//            switch (r) {
//                case PRIVATE: {
//                    //dont show
//                    show = false;
//                    break;
//                }
//                case PROTECTED: {
//                    //show if admin
//                    show = admin || entity.getPersistenceUnit().getSecurityManager().isAllowedRead(field);
//                    break;
//                }
//                default: {
//
//                    show = true;
//                    break;
//                }
//            }
            if (show) {
                SelField sf = new SelField();
                sf.setField(field);
                sf.setLabel(i18n.get(field));
                sf.setPos(pos++);
                sf.setSelected(selectedFieldNames.contains(field.getName()));
                fields.add(sf);
            }
        }
    }

    public List<SelField> getFields() {
        return fields;
    }

    @Override
    public List<net.vpc.upa.Field> getVisibleFields() {

        List<net.vpc.upa.Field> result = new ArrayList<>();
        for (SelField field : fields) {
            if (field.isSelected()) {
                result.add(field.getField());
            }
        }
        if (result.isEmpty()) {
            return getEntity().getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY));
        }
        return result;
    }

    public class SelField {

        private Field field;
        private String label;
        private boolean selected;
        private int pos;

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

    }
}
