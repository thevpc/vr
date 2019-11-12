/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf.ctrl;

import net.vpc.app.vainruling.core.service.editor.EditorFieldSelection;
import net.vpc.upa.AccessMode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    protected void updateMode(AccessMode m) {
        getModel().setMode(m);
    }

    public void onNew() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(AccessMode.PERSIST);
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
            updateMode(AccessMode.UPDATE);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onCancelCurrent() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(AccessMode.READ);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isListMode() {
        try {

            return getModel().getMode() == AccessMode.READ;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewOrUpdateMode() {
        try {
            return getModel().getMode() == AccessMode.PERSIST || getModel().getMode() == AccessMode.UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewMode() {
        try {
            return getModel().getMode() == AccessMode.PERSIST;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isUpdateMode() {
        try {
            return getModel().getMode() == AccessMode.UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isEnabledButton(String buttonId) {
        try {
            if ("Refresh".equals(buttonId)) {
                return getModel().getMode() == AccessMode.READ;
            }
            if ("Persist".equals(buttonId)) {
                return getModel().getMode() == AccessMode.READ;
            }
            if ("Save".equals(buttonId)) {
                return getModel().getMode() != AccessMode.READ;
            }
            if ("Remove".equals(buttonId)
                    || "archive".equals(buttonId)) {
                return getModel().getMode() == AccessMode.UPDATE;
            }
            if ("Cancel".equals(buttonId)) {
                return getModel().getMode() != AccessMode.READ;
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

        private AccessMode mode = AccessMode.READ;
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

        public AccessMode getMode() {
            return mode;
        }

        public void setMode(AccessMode mode) {
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
