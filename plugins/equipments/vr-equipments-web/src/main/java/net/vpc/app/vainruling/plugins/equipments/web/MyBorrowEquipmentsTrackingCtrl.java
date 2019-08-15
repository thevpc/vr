/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
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
        url = "modules/equipments/my-borrow-equipments-tracking",
        securityKey = EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_RETURN_BORROWED
)
@ManagedBean
public class MyBorrowEquipmentsTrackingCtrl {

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
        CorePlugin core = CorePlugin.get();
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        AppUser u = core.getCurrentUser();
        List<AppDepartment> departments = new ArrayList<>(CorePlugin.get().findDepartments());
        for (Iterator<AppDepartment> it = departments.iterator(); it.hasNext();) {
            AppDepartment department = it.next();
            if (core.isCurrentSessionAdmin()) {
                //ok
            } else {
                AppDepartment dd = u.getDepartment();
                if (dd == null || dd.getId() != department.getId()) {
                    it.remove();
                }
            }
        }
        getModel().setDepartments(Vr.get().toEntitySelectItemsNullable(departments, "AppDepartment"));
        if (!VrWebHelper.containsSelectItemId(getModel().getDepartments(), getModel().getDepartment() == null ? null : getModel().getDepartment().getId())) {
            getModel().setDepartment(null);
        }
        onChangeDepartment();
    }

    public void onChangeDepartment() {
        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        AppDepartment d = getModel().getDepartment();
        if (d != null) {
            getModel().setUsers(Vr.get().toEntitySelectItemsNullable(ebs.findBorrowerUsers(d.getId()), "AppUser"));
        } else {
            getModel().setUsers(Vr.get().toEntitySelectItemsNullable(ebs.findBorrowerUsers(null), "AppUser"));
        }
        if (!VrWebHelper.containsSelectItemId(getModel().getUsers(), getModel().getUser() == null ? null : getModel().getUser().getId())) {
            getModel().setUser(null);
        }
        onChangeUser();
    }

    public void onChangeUser() {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        AppUser u = getModel().getUser();
        if (u != null) {
            getModel().setEquipments(ebs.findBorrowedEquipmentsInfo(u.getId()));
        } else {
            getModel().setEquipments(ebs.findBorrowedEquipmentsInfo(null));
        }
    }

    public void onRowSelect(SelectEvent event) {
        EquipmentForResponsibleInfo e = (EquipmentForResponsibleInfo) event.getObject();
        if (e != null) {
            getModel().setQuantity(e.getBorrowQuantity());
        } else {
            getModel().setQuantity(1.0);
        }
    }

    public void onReturnEquipment() {
        EquipmentForResponsibleInfo r = getModel().getSelectedEquipment();
        if (r != null && getModel().getQuantity() > 0) {
//            EquipmentPlugin eqm = EquipmentPlugin.get();
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            try {
                ebs.returnBorrowed(getModel().getSelectedEquipment().getEquipment().getId(), null, null, getModel().getQuantity(), null, null, null);
                getModel().setQuantity(0);
            } catch (Exception ex) {
                FacesUtils.addErrorMessage(ex);
            }
            onRefresh();
        }
    }

    public static class Model {

        private List<EquipmentForResponsibleInfo> equipments;
        private AppDepartment department;
        private AppUser user;
        private List<SelectItem> departments;
        private List<SelectItem> users;
        private EquipmentForResponsibleInfo selectedEquipment;
        private double quantity;

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public AppDepartment getDepartment() {
            return department;
        }

        public void setDepartment(AppDepartment department) {
            this.department = department;
        }

        public AppUser getUser() {
            return user;
        }

        public void setUser(AppUser user) {
            this.user = user;
        }

        public List<SelectItem> getDepartments() {
            return departments;
        }

        public void setDepartments(List<SelectItem> departments) {
            this.departments = departments;
        }

        public List<SelectItem> getUsers() {
            return users;
        }

        public void setUsers(List<SelectItem> users) {
            this.users = users;
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
