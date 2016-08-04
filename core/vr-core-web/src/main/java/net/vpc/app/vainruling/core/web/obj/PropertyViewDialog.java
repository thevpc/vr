/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

/**
 * @author taha.bensalah@gmail.com
 */
public interface PropertyViewDialog {

    String getControlType();

    void openDialog(PropertyView propertyView, String userInfo);

}
