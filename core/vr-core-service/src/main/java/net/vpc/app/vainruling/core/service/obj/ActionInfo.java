/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.obj;

/**
 * @author vpc
 */
public abstract class ActionInfo {

    public String id;
    public String label;
    public String style;

    public ActionInfo(String id, String label, String style) {
        this.id = id;
        this.label = label;
        this.style = style;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getStyle() {
        return style;
    }

    public boolean isEnabled(Object o) {
        return true;
    }

    public abstract <T> T invoke(Class entityType, String actionName, Object obj, Object[] args);
}
