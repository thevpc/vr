/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import java.util.List;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.plugins.equipments.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.service.model.Equipment;
import net.vpc.app.vainruling.plugins.equipments.service.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.service.model.EquipmentBrand;
import net.vpc.app.vainruling.plugins.equipments.service.model.EquipmentType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vpc
 */
public class MyEquipmentBorrowRequests {

    @Autowired
    private CorePlugin core;
    private EquipmentPlugin eq;
    private Model model = new Model();

    @OnPageLoad
    private void onInit() {
        getModel().setCurrentUser(core.getCurrentUser());
        onReloadRequests();
    }

    private void onReloadRequests() {
        getModel().setRequests(eq.findEquipmentRequestsByBorrowerUser(getModel().getCurrentUser().getId(), getModel().isRequestHistory()));
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private AppUser currentUser;
        private boolean requestHistory;
        private Equipment filterListEquipment;
        private EquipmentBrand filterListEquipmentBrand;
        private EquipmentType filterListEquipmentType;
        private AppDepartment filterListEquipmentDepartment;
        private List<Equipment> equipments;
        private List<EquipmentBorrowRequest> requests;

        public boolean isRequestHistory() {
            return requestHistory;
        }

        public void setRequestHistory(boolean requestHistory) {
            this.requestHistory = requestHistory;
        }

        public AppUser getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(AppUser currentUser) {
            this.currentUser = currentUser;
        }

        public Equipment getFilterListEquipment() {
            return filterListEquipment;
        }

        public void setFilterListEquipment(Equipment filterListEquipment) {
            this.filterListEquipment = filterListEquipment;
        }

        public EquipmentBrand getFilterListEquipmentBrand() {
            return filterListEquipmentBrand;
        }

        public void setFilterListEquipmentBrand(EquipmentBrand filterListEquipmentBrand) {
            this.filterListEquipmentBrand = filterListEquipmentBrand;
        }

        public EquipmentType getFilterListEquipmentType() {
            return filterListEquipmentType;
        }

        public void setFilterListEquipmentType(EquipmentType filterListEquipmentType) {
            this.filterListEquipmentType = filterListEquipmentType;
        }

        public AppDepartment getFilterListEquipmentDepartment() {
            return filterListEquipmentDepartment;
        }

        public void setFilterListEquipmentDepartment(AppDepartment filterListEquipmentDepartment) {
            this.filterListEquipmentDepartment = filterListEquipmentDepartment;
        }

        public List<Equipment> getEquipments() {
            return equipments;
        }

        public void setEquipments(List<Equipment> equipments) {
            this.equipments = equipments;
        }

        public List<EquipmentBorrowRequest> getRequests() {
            return requests;
        }

        public void setRequests(List<EquipmentBorrowRequest> requests) {
            this.requests = requests;
        }

    }
}
