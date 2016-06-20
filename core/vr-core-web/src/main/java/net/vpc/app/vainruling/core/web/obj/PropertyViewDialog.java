/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

/**
 * @author vpc
 */
public interface PropertyViewDialog {

    public String getControlType();

    public void openDialog(PropertyView propertyView, String userInfo);

}
