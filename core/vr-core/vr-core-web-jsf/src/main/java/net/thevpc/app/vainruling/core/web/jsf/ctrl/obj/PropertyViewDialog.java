/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

/**
 * @author taha.bensalah@gmail.com
 */
public interface PropertyViewDialog {

    String getControlType();

    boolean acceptAction(String action);

    void openDialog(PropertyView propertyView, String action, String userInfo);

}
