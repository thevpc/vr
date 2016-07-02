/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.filters.Fields;

import java.util.*;

/**
 * @author vpc
 */
public class ObjFieldFieldSelection extends ObjFieldSelection {

    private List<SelField> fields = new ArrayList<>();

    public ObjFieldFieldSelection() {
        super("list");
    }

    public ObjFieldFieldSelection(Entity entity, String[] fields) {
        super("list");
        updateFields(entity, new HashSet<String>(Arrays.asList(fields)));
    }

    @Override
    public void prepare(Entity entity) {
        setEntity(entity);
        Entity old = getEntity();
        Set<String> oldSelection = new HashSet<>();
        if (old == null || !old.getName().equals(entity.getName())) {
            //do nothing!
        } else {
            for (SelField field : fields) {
                if (field.isSelected()) {
                    oldSelection.add(field.getField().getName());
                }
            }
        }
        updateFields(entity, oldSelection);
    }

    private void updateFields(Entity entity, Set<String> selectedFieldNames) {
        fields.clear();
        int pos = 0;
        boolean admin = VrApp.getBean(CorePlugin.class).isUserSessionAdmin();
        I18n i18n = VrApp.getBean(I18n.class);
        for (Field field : entity.getFields()) {
            //should test on field visibility
            AccessLevel r = field.getReadAccessLevel();
            boolean show = false;
            switch (r) {
                case PRIVATE: {
                    //dont show
                    show = false;
                    break;
                }
                case PROTECTED: {
                    //show if admin
                    show = admin;
                    break;
                }
                default: {
                    show = true;
                    break;
                }
            }
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
            return getEntity().getFields(Fields.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY));
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
