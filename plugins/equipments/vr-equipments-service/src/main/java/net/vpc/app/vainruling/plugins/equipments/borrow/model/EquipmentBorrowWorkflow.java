/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.borrow.model;

import net.vpc.upa.config.*;

/**
 * This entity describes a validated/confirmed Borrow of an equipment. Each
 * instance of EquipmentBorrowLog is bound an instance of EquipmentStatusLog
 * that describes a change in the equipment status! An EquipmentBorrowLog has
 * also a set (on or multiple) children of type EquipmentReturnBorrowedLog which
 * define the partial/total return of the quantities.
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "this.name")
@Path("Equipment/Details/Borrow")
public class EquipmentBorrowWorkflow {

    @Path("Main")
    @Id
    @Sequence
    private int id;

    @Main
    private String name;

    @Summary
    private boolean requireUser;
    @Summary
    private boolean requireOperator;
    @Summary
    private boolean requireSuperOperator;

    /**
     * bean implementing
     */
    @Summary
    private String impl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public boolean isRequireUser() {
        return requireUser;
    }

    public void setRequireUser(boolean requireUser) {
        this.requireUser = requireUser;
    }

    public boolean isRequireOperator() {
        return requireOperator;
    }

    public void setRequireOperator(boolean requireOperator) {
        this.requireOperator = requireOperator;
    }

    public boolean isRequireSuperOperator() {
        return requireSuperOperator;
    }

    public void setRequireSuperOperator(boolean requireSuperOperator) {
        this.requireSuperOperator = requireSuperOperator;
    }

}
