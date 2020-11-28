/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.editor.EditorFieldSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.thevpc.app.vainruling.VrAccessMode;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractObjectCtrl<T> extends BasePageCtrl {

    private static final Logger log = Logger.getLogger(AbstractObjectCtrl.class.getName());
    protected Model<T> model;

    protected AbstractObjectCtrl(Model<T> model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    protected abstract T delegated_newInstance();

    public void reloadPage() {
        reloadPage(true);
    }

    public void reloadPage(boolean enableCustomization) {
        try {
            reloadPage(getModel().getCmd(), enableCustomization);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void reloadPage(String cmd, boolean enableCustomization) {

    }

    protected void updateMode(VrAccessMode m) {
        getModel().setMode(m);
    }

    public void onNew() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(VrAccessMode.PERSIST);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onBulkUpdate() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(VrAccessMode.BULK_UPDATE);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onRefresh() {
        reloadPage(true);
    }

    public void onSelect(Object o) {
        getModel().setCurrent(o);
        onSelectCurrent();
    }

    public void onSelectCurrent() {
        try {
            updateMode(VrAccessMode.UPDATE);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onCancelCurrent() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(VrAccessMode.READ);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isListMode() {
        try {

            return getModel().getMode() == VrAccessMode.READ;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isBulkUpdateMode() {
        try {
            return getModel().getMode() == VrAccessMode.BULK_UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }
    public boolean isNewOrUpdateMode() {
        try {
            return getModel().getMode() == VrAccessMode.PERSIST 
                    || getModel().getMode() == VrAccessMode.UPDATE
                    || getModel().getMode() == VrAccessMode.BULK_UPDATE
                    ;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewMode() {
        try {
            return getModel().getMode() == VrAccessMode.PERSIST;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isUpdateMode() {
        try {
            return getModel().getMode() == VrAccessMode.UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isEnabledButton(String buttonId) {
        try {
            if ("Refresh".equals(buttonId)) {
                return getModel().getMode() == VrAccessMode.READ;
            }
            if ("Persist".equals(buttonId)) {
                return getModel().getMode() == VrAccessMode.READ;
            }
            if ("Save".equals(buttonId)) {
                return getModel().getMode() != VrAccessMode.READ;
            }
            if ("Remove".equals(buttonId)
                    || "archive".equals(buttonId)) {
                return getModel().getMode() == VrAccessMode.UPDATE;
            }
            if ("Cancel".equals(buttonId)) {
                return getModel().getMode() != VrAccessMode.READ;
            }
            return false;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public interface SelectionListener<T> {

        void onSelect(T c);
    }

    public static class Model<T> {

        private VrAccessMode mode = VrAccessMode.READ;
        private T current;
        private String cmd;
        private EditorFieldSelection fieldSelection;
        private List<T> list = new ArrayList<>();
        private List<T> selectedRows = new ArrayList<>();

        public EditorFieldSelection getFieldSelection() {
            return fieldSelection;
        }

        public void setFieldSelection(EditorFieldSelection fieldSelection) {
            this.fieldSelection = fieldSelection;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public T getCurrent() {
            return current;
        }

        public void setCurrent(T current) {
            this.current = current;
        }

        public VrAccessMode getMode() {
            return mode;
        }

        public void setMode(VrAccessMode mode) {
            this.mode = mode;
        }

        public List<T> getList() {
            return list;
        }

        public List<T> getSelectedRows() {
            return selectedRows;
        }

        public void setSelectedRows(List<T> selectedRows) {
            this.selectedRows = selectedRows;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

    }
}
