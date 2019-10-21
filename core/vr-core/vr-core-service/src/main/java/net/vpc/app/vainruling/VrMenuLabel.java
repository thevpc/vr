/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling;


/**
 * @author taha.bensalah@gmail.com
 */
public class VrMenuLabel {

    private String name;
    private String type;

    public VrMenuLabel(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
