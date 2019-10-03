/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.core.model.EquipmentType;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import net.vpc.common.jsf.FacesUtils;
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
        url = "modules/equipments/my-borrowable-equipments",
         securityKey = EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROWABLE
)
@ManagedBean
public class MyBorrowableEquipmentsCtrl {

    private Model model = new Model();

    public Model getModel() {
        return model;
    }

    @OnPageLoad
//    @PostConstruct
    public void onpageLoad() {
        onRefresh();
        onClearForm();
    }

    public void onRefresh() {
        EquipmentType p = getModel().getFilterEquipmentType();
        AppDepartment d = getModel().getFilterDepartment();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        getModel().setEquipments(ebs.findBorrowableEquipmentsForResponsibleInfo(CorePlugin.get().getCurrentUserIdFF(),
                p == null ? null : p.getId(),
                d == null ? null : d.getId()
        ));
        getModel().setVisaUsers(Vr.get().toEntitySelectItemsNullable(ebs.findBorrowVisaUsers(), "AppUser"));
        onClearForm();
    }

    public void onClearForm() {
        getModel().setFromDate(null);
        getModel().setToDate(null);
        getModel().setVisaUser(null);
        getModel().setQuantity(1.0);
        getModel().setQuantity(1.0);
    }

    public void onAddRequest() {
        CorePlugin core = CorePlugin.get();
        EquipmentBorrowRequest req = new EquipmentBorrowRequest();
        req.setBorrowerUser(core.getCurrentUser());
        req.setEquipment(getModel().getSelectedEquipment() == null ? null : getModel().getSelectedEquipment().getEquipment());
        req.setFromDate(getModel().getFromDate());
        req.setToDate(getModel().getToDate());
        req.setVisaUser(getModel().getVisaUser());
        req.setQuantity(getModel().getQuantity());
        try {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            ebs.addEquipmentBorrowRequest(req);
            onClearForm();
            FacesUtils.addInfoMessage("Reservation "+(getModel().getSelectedEquipment().getName())+" réussie");
        } catch (Exception ex) {
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onChangeFilter() {
        onRefresh();
        //
    }

    public void onRowSelect(SelectEvent event) {
//        getModel().setSelectedEquipment(((EquipmenForResponsibleInfo) event.getObject()));
    }

    public static class Model {

        private List<EquipmentForResponsibleInfo> equipments;
        private EquipmentType filterEquipmentType;
        private AppDepartment filterDepartment;
        private EquipmentForResponsibleInfo selectedEquipment;
        private Date fromDate;
        private Date toDate;
        private double quantity;
        private AppUser visaUser;
        private List<SelectItem> visaUsers;

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public EquipmentForResponsibleInfo getSelectedEquipment() {
            return selectedEquipment;
        }

        public void setSelectedEquipment(EquipmentForResponsibleInfo selectedEquipment) {
            this.selectedEquipment = selectedEquipment;
        }

        public Date getFromDate() {
            return fromDate;
        }

        public void setFromDate(Date fromDate) {
            this.fromDate = fromDate;
        }

        public Date getToDate() {
            return toDate;
        }

        public void setToDate(Date toDate) {
            this.toDate = toDate;
        }

        public AppUser getVisaUser() {
            return visaUser;
        }

        public void setVisaUser(AppUser visaUser) {
            this.visaUser = visaUser;
        }

        public List<SelectItem> getVisaUsers() {
            return visaUsers;
        }

        public void setVisaUsers(List<SelectItem> visaUsers) {
            this.visaUsers = visaUsers;
        }

        public List<EquipmentForResponsibleInfo> getEquipments() {
            return equipments;
        }

        public void setEquipments(List<EquipmentForResponsibleInfo> equipments) {
            this.equipments = equipments;
        }

        public EquipmentType getFilterEquipmentType() {
            return filterEquipmentType;
        }

        public void setFilterEquipmentType(EquipmentType filterEquipmentType) {
            this.filterEquipmentType = filterEquipmentType;
        }

        public AppDepartment getFilterDepartment() {
            return filterDepartment;
        }

        public void setFilterDepartment(AppDepartment filterDepartment) {
            this.filterDepartment = filterDepartment;
        }

    }
}
