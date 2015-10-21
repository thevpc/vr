/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.security;

import net.vpc.upa.AccessLevel;
import net.vpc.upa.Field;
import net.vpc.upa.callbacks.EntityDefinitionListener;
import net.vpc.upa.callbacks.EntityEvent;
import net.vpc.upa.callbacks.FieldDefinitionListener;
import net.vpc.upa.callbacks.FieldEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.exceptions.UPAException;

/**
 *
 * @author vpc
 */
@Callback
public class VRDefaultFieldsCallback implements FieldDefinitionListener{

    @Override
    public void onPreCreateField(FieldEvent event) throws UPAException {
        Field f = event.getField();
        String name = f.getName();
        if(
                name.equals("deleted")
                ||name.equals("deletedOn")
                ||name.equals("deletedBy")
                ||name.equals("archived")
                ||name.equals("archivedOn")
                ||name.equals("archivedBy")
                ){
            f.setPersistAccessLevel(AccessLevel.PRIVATE);
            f.setUpdateAccessLevel(AccessLevel.PROTECTED);
            f.setReadAccessLevel(AccessLevel.PROTECTED);
        }
    }

    @Override
    public void onCreateField(FieldEvent event) throws UPAException {
    
    }

    @Override
    public void onPreDropField(FieldEvent event) throws UPAException {
    
    }

    @Override
    public void onDropField(FieldEvent event) throws UPAException {
    
    }

    @Override
    public void onPreMoveField(FieldEvent event) throws UPAException {
    
    }

    @Override
    public void onMoveField(FieldEvent event) throws UPAException {
    
    }

    
    
}
