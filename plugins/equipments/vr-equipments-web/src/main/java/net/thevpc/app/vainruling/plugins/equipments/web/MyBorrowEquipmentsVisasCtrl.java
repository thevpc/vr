/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.web;

import java.util.List;
import javax.faces.bean.ManagedBean;

import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequestStatus;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowVisaStatus;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowOperatorType;
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.thevpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import net.thevpc.app.vainruling.core.service.VrApp;
import org.primefaces.event.SelectEvent;
import net.thevpc.app.vainruling.VrPage;

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

    @VrOnPageLoad
//    @PostConstruct
    public void onpageLoad() {
        onRefresh();
    }

    public void onRefresh() {
        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        Integer u = CorePlugin.get().getCurrentUserId();
        getModel().setVisa(ebs.isBorrowVisaUser(u));
        getModel().setOperator(ebs.isBorrowOperatorUser(u));
        getModel().setSuperOperator(ebs.isBorrowSuperOperatorUser(u));
        onRefreshEquipments();
    }

    public void onRefreshEquipments() {
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        Integer u = CorePlugin.get().getCurrentUserId();
        getModel().setEquipments(ebs.findBorrowEquipmentsVisasForResponsibleInfo(u));
    }

    public boolean isVisaVisible(EquipmentForResponsibleInfo info) {
        if (info == null || info.getRequest().getVisaUser() == null) {
            return false;
        }
        return true;
    }

    public boolean isVisaAccept(EquipmentForResponsibleInfo info, boolean accept) {
        if (info == null || info.getRequest().getVisaUserStatus() == null || !getModel().isVisa()) {
            return false;
        }
        if (info.getVisaUser() == null || info.getVisaUser().getId() != CorePlugin.get().getCurrentUserId()) {
            return false;
        }
        if (info.getRequest().isArchive()) {
            return false;
        }
        if (info.getRequest().getFinalStatus() == EquipmentBorrowRequestStatus.BORROWED
                || info.getRequest().getFinalStatus() == EquipmentBorrowRequestStatus.RETURNED) {
            return false;
        }
        switch (info.getRequest().getVisaUserStatus()) {
            case EquipmentBorrowVisaStatus.ACCEPTED:
                return !accept;
            case EquipmentBorrowVisaStatus.REJECTED:
                return accept;
            case EquipmentBorrowVisaStatus.PENDING:
                return true;
            case EquipmentBorrowVisaStatus.IGNORED:
                return false;
        }
        return false;
    }

    public boolean isOperatorAccept(EquipmentForResponsibleInfo info, boolean accept) {
        if (info == null || info.getRequest().getOperatorUserStatus() == null || !getModel().isOperator()) {
            return false;
        }
        if (info.getRequest().isArchive()) {
            return false;
        }
        if (info.getRequest().getFinalStatus() == EquipmentBorrowRequestStatus.BORROWED
                || info.getRequest().getFinalStatus() == EquipmentBorrowRequestStatus.RETURNED) {
            return false;
        }
        switch (info.getRequest().getOperatorUserStatus()) {
            case EquipmentBorrowVisaStatus.ACCEPTED:
                return !accept;
            case EquipmentBorrowVisaStatus.REJECTED:
                return accept;
            case EquipmentBorrowVisaStatus.PENDING:
                return true;
            case EquipmentBorrowVisaStatus.IGNORED:
                return false;
        }
        return false;
    }

    public boolean isSuperOperatorAccept(EquipmentForResponsibleInfo info, boolean accept) {
        if (info == null || info.getRequest().getSuperOperatorUserStatus() == null || !getModel().isSuperOperator()) {
            return false;
        }
        if (info.getRequest().isArchive()) {
            return false;
        }
        if (info.getRequest().getFinalStatus() == EquipmentBorrowRequestStatus.BORROWED
                || info.getRequest().getFinalStatus() == EquipmentBorrowRequestStatus.RETURNED) {
            return false;
        }
        switch (info.getRequest().getSuperOperatorUserStatus()) {
            case EquipmentBorrowVisaStatus.ACCEPTED:
                return !accept;
            case EquipmentBorrowVisaStatus.REJECTED:
                return accept;
            case EquipmentBorrowVisaStatus.PENDING:
                return true;
            case EquipmentBorrowVisaStatus.IGNORED:
                return false;
        }
        return false;
    }

    public boolean isDeliver(EquipmentForResponsibleInfo info) {
        if (info == null || info.getRequest() == null) {
            return false;
        }
        if (info.getRequest().getBorrow() != null) {
            return false;
        }
        if (info.getRequest().getSuperOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED) {
            if (info.getRequest().getBorrow() == null) {
                return true;
            }
        }
        if (info.getRequest().getSuperOperatorUserStatus() != EquipmentBorrowVisaStatus.REJECTED
                && info.getRequest().getOperatorUserStatus() == EquipmentBorrowVisaStatus.ACCEPTED) {
            return true;
        }
        return false;
    }

    public boolean isArchive(EquipmentForResponsibleInfo info) {
        if (info == null
                || (!getModel().isSuperOperator() && !getModel().isOperator())) {
            return false;
        }
        if (info.getRequest().getFinalStatus() == null) {
            return false;
        }
        if (info.getRequest().isArchive()) {
            return false;
        }
        switch (info.getRequest().getFinalStatus()) {
            case EquipmentBorrowRequestStatus.PENDING:
            case EquipmentBorrowRequestStatus.ACCEPTED:
            case EquipmentBorrowRequestStatus.BORROWED: {
                return false;
            }
            case EquipmentBorrowRequestStatus.REJECTED:
            case EquipmentBorrowRequestStatus.RETURNED: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public void onChangeFilter() {
        onRefresh();
        //
    }

    public void onRowSelect(SelectEvent event) {
//        getModel().setSelectedEquipment(((EquipmenForResponsibleInfo) event.getObject()));
    }

    public void onAcceptVisa(EquipmentForResponsibleInfo info, boolean accept) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.applyVisa(info.getRequest().getId(), EquipmentBorrowOperatorType.USER, null, accept, false);
        onRefreshEquipments();
    }

    public void onArchive(EquipmentForResponsibleInfo info) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.archiveRequest(info.getRequest().getId(), null);
        onRefreshEquipments();
    }

    public void onAcceptOperator(EquipmentForResponsibleInfo info, boolean accept) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.applyVisa(info.getRequest().getId(), EquipmentBorrowOperatorType.OPERATOR, null, accept, false);
        onRefreshEquipments();
    }

    public void onAcceptSuperOperator(EquipmentForResponsibleInfo info, boolean accept) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.applyVisa(info.getRequest().getId(), EquipmentBorrowOperatorType.SUPER_OPERATOR, null, accept, false);
        onRefreshEquipments();
    }

    public void onDeliver(EquipmentForResponsibleInfo info) {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        ebs.deliverOrDeliverBackEquipment(info.getRequest().getId(), null);
        onRefreshEquipments();
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
