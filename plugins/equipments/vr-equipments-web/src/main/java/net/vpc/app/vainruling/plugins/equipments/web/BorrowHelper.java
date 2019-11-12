/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequestStatus;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowStatusExt;

/**
 *
 * @author vpc
 */
@ManagedBean
@ApplicationScoped
public class BorrowHelper {

    public String getStyle(EquipmentBorrowRequestStatus s) {
        if (s == null) {
            return "";
        }
        //'danger','success','info','warning','primary','default'
        switch (s) {
            case ACCEPTED:
                return "success";
            case PENDING:
                return "default";
            case REJECTED:
                return "danger";
            case RETURNED:
                return "info";
        }
        return "default";
    }

    public String getStyle(EquipmentBorrowVisaStatus s) {
        if (s == null) {
            return "";
        }
        //'danger','success','info','warning','primary','default'
        switch (s) {
            case ACCEPTED:
                return "success";
            case PENDING:
                return "default";
            case REJECTED:
                return "danger";
            case IGNORED:
                return "warning";
        }
        return "default";
    }

    public String getStyle(EquipmentBorrowStatusExt s) {
        if (s == null) {
            return "";
        }
        //'danger','success','info','warning','primary','default'
        switch (s) {
            case ACCEPTED:
                return "success";
            case PENDING:
                return "default";
            case REJECTED:
                return "danger";
            case RETURN_IMMEDIATELY:
                return "warning";
            case BORROWED:
                return "info";
            case RETURNED:
                return "primary";
            case RETURN_LATER:
                return "info";
        }
        return "default";
    }
}
