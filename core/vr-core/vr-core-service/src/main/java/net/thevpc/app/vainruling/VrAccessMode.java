/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

import net.thevpc.upa.AccessMode;

/**
 *
 * @author vpc
 */
public enum VrAccessMode {
    DEFAULT(AccessMode.DEFAULT),
    PERSIST(AccessMode.PERSIST),
    UPDATE(AccessMode.UPDATE),
    READ(AccessMode.READ),
    BULK_UPDATE(AccessMode.UPDATE);

    private AccessMode m;

    private VrAccessMode(AccessMode m) {
        this.m = m;
    }
    
    public AccessMode toUpaMode(){
        return m;
    }
}
