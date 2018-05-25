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
import net.vpc.common.strings.StringUtils;

/**
 * @author taha.bensalah@gmail.com
 */
public class ObjFieldFieldSelection extends ObjFieldSelection {

    private AccessMode mode = null;
    private List<SelField> fields = new ArrayList<>();

    public ObjFieldFieldSelection() {
        super("list");
    }

    public ObjFieldFieldSelection(Entity entity, String[] fields, AccessMode mode, boolean requireSave) {
        super("list");

        updateFields(mode, entity, new HashSet<String>(Arrays.asList(fields)), requireSave);
    }

    @Override
    public void load() {
        Entity old = getEntity();
        if (old != null) {
            prepare(old, AccessMode.READ);
        }
    }

    @Override
    public void save() {
        Entity old = getEntity();
        if (old != null) {
            Set<String> oldSelection = new HashSet<>();
            for (SelField field : fields) {
                if (field.isSelected()) {
                    oldSelection.add(field.getField().getName());
                }
            }
            saveVisibleFields(oldSelection);
        }
    }

    @Override
    public void reset() {
        Entity old = getEntity();
        if (old != null) {
            Set<String> oldSelection = new HashSet<>();
            saveVisibleFields(oldSelection);
            prepare(old, mode);
        }
    }

    @Override
    public void prepare(Entity entity, AccessMode mode) {
        this.mode = mode;
        Entity old = getEntity();
        setEntity(entity);
        Set<String> oldSelection = null;
//        if (old == null || !old.getName().equals(entity.getName())) {
        oldSelection = loadVisibleFields();
        boolean requireSave = false;
        if (oldSelection == null || oldSelection.isEmpty()) {
            requireSave = true;
            //do nothing!
            oldSelection = new HashSet<>();
            for (Field field : getEntity().getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY))) {
                oldSelection.add(field.getName());
            }
        }
        updateFields(mode, entity, oldSelection, requireSave);

//        } else {
//            oldSelection = new HashSet<>();
//            for (SelField field : fields) {
//                if (field.isSelected()) {
//                    oldSelection.add(field.getField().getName());
//                }
//            }
//            if(oldSelection.isEmpty()){
//                for (Field field : getEntity().getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY))) {
//                    oldSelection.add(field.getName());
//                }
//            }
//            updateFields(mode, entity, oldSelection);
//        }
    }

    private void updateFields(AccessMode mode, Entity entity, Set<String> selectedFieldNames, boolean requireSave) {
        fields.clear();
        int pos = 0;
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        boolean admin = core.isCurrentSessionAdmin();
        I18n i18n = VrApp.getBean(I18n.class);
        Set<String> selectedFields = new HashSet<>();
        for (Field field : entity.getFields()) {
            //should test on field visibility
            AccessLevel r = field.getReadAccessLevel();
            AccessLevel u = field.getUpdateAccessLevel();
            boolean show = false;
            switch (mode) {
                case READ: {
                    show = field.getEffectiveReadAccessLevel() != AccessLevel.INACCESSIBLE;
                    break;
                }
                case UPDATE: {
                    show = field.getEffectiveUpdateAccessLevel() != AccessLevel.INACCESSIBLE;
                    break;
                }
                case PERSIST: {
                    show = field.getEffectivePersistAccessLevel() != AccessLevel.INACCESSIBLE;
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
                sf.setLabel(field.getTitle());
                sf.setPos(pos++);
                sf.setSelected(selectedFieldNames.contains(field.getName()));
                fields.add(sf);
                if (selectedFieldNames.contains(field.getName())) {
                    selectedFields.add(field.getName());
                }
            }
        }
        if (requireSave /*|| selectedFields.equals(selectedFieldNames)*/) {
            saveVisibleFields(selectedFields);
        }
//        if (!StringUtils.isEmpty(core.getCurrentUserLogin())) {
//            core.setAppProperty("System.View." + entity.getName() + ".VisibleFields", core.getCurrentUserLogin(), core);
//        }
    }

    public void saveVisibleFields(Set<String> fields) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (!StringUtils.isEmpty(core.getCurrentUserLogin())) {
            if (fields == null || fields.isEmpty()) {
                core.setAppProperty("System.View." + getEntity().getName() + ".VisibleFields", core.getCurrentUserLogin(), null);
            } else {
                core.setAppProperty("System.View." + getEntity().getName() + ".VisibleFields", core.getCurrentUserLogin(), net.vpc.common.strings.StringUtils.listToString(fields, ";"));
            }
        }
    }

    public void resetVisibleFields(Set<String> fields) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        if (!StringUtils.isEmpty(core.getCurrentUserLogin())) {
            core.setAppProperty("System.View." + getEntity().getName() + ".VisibleFields", core.getCurrentUserLogin(), net.vpc.common.strings.StringUtils.listToString(fields, ","));
        }
    }

    private Set<String> loadVisibleFields() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        String s = (String) core.getAppPropertyValue("System.View." + getEntity().getName() + ".VisibleFields", core.getCurrentUserLogin());
        if (s != null) {
            return new HashSet<String>(Arrays.asList(StringUtils.split(s, ",;", true, true)));
        }
        return null;
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
