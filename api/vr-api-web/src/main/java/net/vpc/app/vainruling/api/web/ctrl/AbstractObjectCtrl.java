/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.ctrl;

import net.vpc.app.vainruling.api.core.ObjSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.web.VrMenuManager;

/**
 *
 * @author vpc
 */
public abstract class AbstractObjectCtrl<T> extends BasePageCtrl {

    protected Model<T> model;
    private static final Logger log = Logger.getLogger(AbstractObjectCtrl.class.getName());

    protected AbstractObjectCtrl(Model<T> model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    protected abstract T delegated_newInstance();

//    @PostConstruct
    public void reloadPage() {
        try {
            reloadPage(getModel().getCmd());
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void reloadPage(String cmd) {

    }

    protected void updateMode(EditCtrlMode m) {
        getModel().setMode(m);
    }

    public void onNew() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(EditCtrlMode.NEW);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onRefresh() {
        reloadPage();
    }

    public void onSelect(Object o) {
        getModel().setCurrent(o);
        onSelectCurrent();
    }

    public void onSelectCurrent() {
        try {
            updateMode(EditCtrlMode.UPDATE);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onCancelCurrent() {
        try {
            getModel().setCurrent(delegated_newInstance());
            updateMode(EditCtrlMode.LIST);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isListMode() {
        try {

            return getModel().getMode() == EditCtrlMode.LIST;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewOrUpdateMode() {
        try {
            return getModel().getMode() == EditCtrlMode.NEW || getModel().getMode() == EditCtrlMode.UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isNewMode() {
        try {
            return getModel().getMode() == EditCtrlMode.NEW;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isUpdateMode() {
        try {
            return getModel().getMode() == EditCtrlMode.UPDATE;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public boolean isEnabledButton(String buttonId) {
        try {
            if ("Refresh".equals(buttonId)) {
                return getModel().getMode() == EditCtrlMode.LIST;
            }
            if ("Persist".equals(buttonId)) {
                return getModel().getMode() == EditCtrlMode.LIST;
            }
            if ("Save".equals(buttonId)) {
                return getModel().getMode() != EditCtrlMode.LIST;
            }
            if ("Remove".equals(buttonId)
                    || "archive".equals(buttonId)) {
                return getModel().getMode() == EditCtrlMode.UPDATE;
            }
            if ("Cancel".equals(buttonId)) {
                return getModel().getMode() != EditCtrlMode.LIST;
            }
            return false;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public static class Model<T> {

        private EditCtrlMode mode = EditCtrlMode.LIST;
        private T current;
        private String cmd;
        private ObjSearch search;
        private List<T> list = new ArrayList<>();

        public ObjSearch getSearch() {
            return search;
        }

        public void setSearch(ObjSearch search) {
            this.search = search;
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

        public EditCtrlMode getMode() {
            return mode;
        }

        public void setMode(EditCtrlMode mode) {
            this.mode = mode;
        }

        public void setCurrent(T current) {
            this.current = current;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

    }

    public static interface SelectionListener<T> {

        void onSelect(T c);
    }
}
