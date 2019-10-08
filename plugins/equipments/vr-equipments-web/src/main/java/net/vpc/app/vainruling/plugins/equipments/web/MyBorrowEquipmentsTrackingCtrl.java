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
import net.vpc.common.strings.StringUtils;

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
        if (!VrWebHelper.containsSelectItemId(getModel().getDepartments(), getModel().getDepartment())) {
            getModel().setDepartment(null);
        }
        onChangeDepartment();
    }

    public void onChangeDepartment() {
        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        Integer d = getModel().getDepartment();
        getModel().setUsers(Vr.get().toEntitySelectItemsNullable(ebs.findBorrowerUsers(d, true, false), "AppUser"));
        if (!VrWebHelper.containsSelectItemId(getModel().getUsers(), getModel().getUser())) {
            getModel().setUser(null);
        }
        onChangeUser();
    }

    public void onChangeUser() {
//        EquipmentPlugin eqm = EquipmentPlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        Integer u = getModel().getUser();
        getModel().setEquipments(ebs.findBorrowedEquipmentsInfo(u));
    }

    public void onRowSelect(SelectEvent event) {
        EquipmentForResponsibleInfo e = (EquipmentForResponsibleInfo) event.getObject();
        if (e != null) {
            getModel().setQuantity(e.getBorrowRemainingQuantity());
        } else {
            getModel().setQuantity(1.0);
        }
    }

    public String getSelectedEquipmentString() {
        EquipmentForResponsibleInfo e = getModel().getSelectedEquipment();
        if (e == null) {
            return "?";
        }
        if (e.getEquipment() == null) {
            return "?";
        }
        StringBuilder sb = new StringBuilder();
        if (e.getEquipment().getType() != null && !StringUtils.isBlank(e.getEquipment().getType().getName())) {
            if (sb.length() > 0) {
                sb.append("/");
            }
            sb.append(e.getEquipment().getType().getName());
        }
        if (e.getEquipment().getBrandLine() != null && !StringUtils.isBlank(e.getEquipment().getBrandLine().getName())) {
            if (sb.length() > 0) {
                sb.append("/");
            }
            sb.append(e.getEquipment().getBrandLine().getName());
        }
        if (!StringUtils.isBlank(e.getEquipment().getName())) {
            if (sb.length() > 0) {
                sb.append("/");
            }
            sb.append(e.getEquipment().getName());
        }
        return sb.toString();
    }

    public void onReturnSelectedEquipment(EquipmentForResponsibleInfo r) {
        onReturnSelectedEquipment(r,r.getBorrowRemainingQuantity());
    }

    public void onReturnSelectedEquipment(EquipmentForResponsibleInfo r, double qty) {
        if (r != null && qty > 0) {
//            EquipmentPlugin eqm = EquipmentPlugin.get();
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            try {
                ebs.returnBorrowed(r.getEquipment().getId(), null, null, qty, null, null, null);
                getModel().setQuantity(0);
                getModel().setSelectedEquipment(null);
            } catch (Exception ex) {
                FacesUtils.addErrorMessage(ex);
            }
            onRefresh();
        }
    }

    public void onReturnEquipment() {
        onReturnSelectedEquipment(getModel().getSelectedEquipment(), getModel().getQuantity());
    }

    public static class Model {

        private List<EquipmentForResponsibleInfo> equipments;
        private Integer department;
        private Integer user;
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

        public Integer getDepartment() {
            return department;
        }

        public void setDepartment(Integer department) {
            this.department = department;
        }

        public Integer getUser() {
            return user;
        }

        public void setUser(Integer user) {
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
