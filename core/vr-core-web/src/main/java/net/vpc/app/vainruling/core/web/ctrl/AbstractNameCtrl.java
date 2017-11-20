/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.upa.AccessMode;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class AbstractNameCtrl<T> extends BasePageCtrl {

    private Model<T> model;

    private SelectionListener<T> listener;

    protected AbstractNameCtrl(Model<T> model) {
        this.model = model;
    }

    public Model<T> getModel() {
        return model;
    }

    public void onNew() {
        T nouv = delegated_newInstance();
        delegated_newCurrent();
        getModel().setCurrent(nouv);
        getModel().getList().add(0, getModel().getCurrent());
        getModel().setMode(AccessMode.PERSIST);
    }

    public SelectionListener<T> getListener() {
        return listener;
    }

    public void setListener(SelectionListener<T> listener) {
        this.listener = listener;
    }

    public void onSelectCurrent() {
        getModel().setMode(AccessMode.UPDATE);
        if (listener != null) {
            listener.onSelect(getModel().getCurrent());
        }
    }

    public void onSaveCurrent() {
        delegated_saveCurrent();
        onCancelCurrent();
        reloadPage();
        getModel().setMode(AccessMode.READ);
    }

    public void onDeleteCurrent() {
        delegated_deleteCurrent();
        getModel().setMode(AccessMode.READ);
        reloadPage();
    }

    public void onCancelCurrent() {
        getModel().setCurrent(delegated_newInstance());
        getModel().setMode(AccessMode.READ);
    }

    public void onRowSelect(SelectEvent event) {
        T current = (T) event.getObject();
//        FacesMessage msg = new FacesMessage("row Selected", "" + (current==null?"":current.getId()));
//        FacesContext.getCurrentInstance().addMessage(null, msg);
        getModel().setCurrent(current == null ? delegated_newInstance() : current);
    }

    public void onRowUnselect(UnselectEvent event) {
//        FacesMessage msg = new FacesMessage("Car Unselected", "" + ((T) event.getObject()).getId());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    @PostConstruct
    public void reloadPage() {
        reloadPage(getModel().getCmd());
    }

    public void reloadPage(String cmd) {
        getModel().setCmd(cmd);
        getModel().setCurrent(delegated_newInstance());
        List<T> list = delegated_findAll();
        getModel().setList(list);
        if (list.size() > 0) {
            getModel().setCurrent(list.get(0));
        } else {
            getModel().setCurrent(null);
        }
    }

    protected abstract T delegated_newInstance();

    protected abstract void delegated_newCurrent();

    protected abstract void delegated_saveCurrent();

    protected abstract void delegated_deleteCurrent();

    protected abstract List<T> delegated_findAll();

    public interface SelectionListener<T> {

        void onSelect(T c);
    }

    public static class Model<T> {

        private AccessMode mode = AccessMode.READ;
        private T current;
        private String cmd;
        private List<T> list = new ArrayList<>();

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

        public void setList(List<T> list) {
            this.list = list;
        }

    }
}
