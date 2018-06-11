/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj;

/**
 * @author taha.bensalah@gmail.com
 */
public class ObjFormAction {

    //<p:commandButton value="Ann." rendered="#{objCtrl.isEnabledButton('Assign')}" icon="ui-ribbonicon-canc" styleClass="ui-ribbon-bigbutton" ajax="true" actionListener="#{objCtrl.onAssignCurrent}" update="buttons :listForm :itemForm"/>
    private String type;
    private String value;
    private String icon = "ui-ribbon-bigbutton";
    private String key;
    private String[] command;

    public ObjFormAction(String type, String value, String icon, String key, String[] command) {
        this.type = type;
        this.value = value;
        this.icon = icon;
        this.key = key;
        this.command = command;
    }

    public String getType() {
        return type;
    }

    public String[] getCommand() {
        return command;
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
