/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

/**
 *
 * @author vpc
 */
public interface PropertyViewDialog {

    public String getControlType();

    public void openDialog(PropertyView propertyView, String userInfo);

}
