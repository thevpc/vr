/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import java.util.List;
import javax.faces.bean.ManagedBean;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowOperatorType;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import org.primefaces.event.SelectEvent;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;

//import javax.annotation.PostConstruct;
/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        menu = "/Equipment",
        url = "modules/equipments/my-borrow-equipments-visas",
         securityKey = EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROW_VISA
)
@ManagedBean
public class MyBorrowEquipmentsVisasCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
//    @PostConstruct
    public void onpageLoad() {
        onRefresh();
    }

    public void onRefresh() {
        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        Integer u = CorePlugin.get().getCurrentUserId();
        getModel().setEquipments(ebs.findBorrowEquipmentsVisasForResponsibleInfo(u));
        getModel().setVisa(ebs.isBorrowVisaUser(u));
        getModel().setOperator(ebs.isBorrowOperatorUser(u));
        getModel().setSuperOperator(ebs.isBorrowSuperOperatorUser(u));
    }

    public void onChangeFilter() {
        onRefresh();
        //
    }

    public void onRowSelect(SelectEvent event) {
//        getModel().setSelectedEquipment(((EquipmenForResponsibleInfo) event.getObject()));
    }

    public void onAcceptVisa(boolean accept) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.applyEquipmentRequestByVisaUser(getModel().getSelectedEquipment().getRequest().getId(), EquipmentBorrowOperatorType.VISA, null, accept);
    }

    public void onAcceptOperator(boolean accept) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.applyEquipmentRequestByVisaUser(getModel().getSelectedEquipment().getRequest().getId(), EquipmentBorrowOperatorType.OPERATOR, null, accept);
    }

    public void onAcceptSuperOperator(boolean accept) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.applyEquipmentRequestByVisaUser(getModel().getSelectedEquipment().getRequest().getId(), EquipmentBorrowOperatorType.SUPER_OPERATOR, null, accept);
    }

    public static class Model {

        private boolean visa;
        private boolean operator;
        private boolean superOperator;

        private List<EquipmentForResponsibleInfo> equipments;
        private EquipmentForResponsibleInfo selectedEquipment;

        public boolean isVisa() {
            return visa;
        }

        public void setVisa(boolean visa) {
            this.visa = visa;
        }

        public boolean isOperator() {
            return operator;
        }

        public void setOperator(boolean operator) {
            this.operator = operator;
        }

        public boolean isSuperOperator() {
            return superOperator;
        }

        public void setSuperOperator(boolean superOperator) {
            this.superOperator = superOperator;
        }

        public EquipmentForResponsibleInfo getSelectedEquipment() {
            return selectedEquipment;
        }

        public void setSelectedEquipment(EquipmentForResponsibleInfo selectedEquipment) {
            this.selectedEquipment = selectedEquipment;
        }

        public List<EquipmentForResponsibleInfo> getEquipments() {
            return equipments;
        }

        public void setEquipments(List<EquipmentForResponsibleInfo> equipments) {
            this.equipments = equipments;
        }
    }
}
