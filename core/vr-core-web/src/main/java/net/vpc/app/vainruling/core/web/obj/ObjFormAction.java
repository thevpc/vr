/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

/**
 *
 * @author vpc
 */
public class ObjFormAction {
    //<p:commandButton value="Ann." rendered="#{objCtrl.isEnabledButton('Assign')}" icon="ui-ribbonicon-canc" styleClass="ui-ribbon-bigbutton" ajax="true" actionListener="#{objCtrl.onAssignCurrent}" update="buttons :listForm :itemForm"/>
    private String value;
    private String icon="ui-ribbon-bigbutton";
    private String key;

    public ObjFormAction(String value, String icon, String key) {
        this.value = value;
        this.icon = icon;
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getIcon() {
        return icon;
    }

    public String getKey() {
        return key;
    }
    
    
}
