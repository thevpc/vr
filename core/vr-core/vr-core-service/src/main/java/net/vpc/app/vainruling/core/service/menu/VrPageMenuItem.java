/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.menu;

/**
 *
 * @author vpc
 */
public class VrPageMenuItem {

    public String name;
    public Object value;
    public int priority;

    public VrPageMenuItem(String name, Object value, int priority) {
        this.name = name;
        this.value = value;
        this.priority = priority;
    }
}
