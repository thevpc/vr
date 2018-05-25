/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.service.model;

/**
 * @author taha.bensalah@gmail.com
 */
public enum EquipmentStatusType {


    AVAILABLE(1),
    BORROWED(-1),
    LOST(-1),
    BROKEN(-1),
    UNUSABLE(-1),
    USABLE_WITH_CARE(0),
    TEMPORARILY_UNAVAILABLE(-1),
    PLANNED_INTERVENTION(0),
    INTERVENTION_ON_COMPLAINT(0),
    COMPLAINT(0),
    ACQUISITION(1),
//    BORROWABLE(1),
    INSTORE(1)
    ;

    private int sign;

    EquipmentStatusType(int sign) {
        this.sign = sign;
    }

    public int getSign() {
        return sign;
    }
}
