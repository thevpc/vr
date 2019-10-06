/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.pages.OnPageLoad;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import net.vpc.common.jsf.FacesUtils;
import org.primefaces.event.SelectEvent;
import net.vpc.app.vainruling.core.service.pages.VrPage;
import net.vpc.app.vainruling.core.service.pages.VrPathItem;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowOperatorType;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPlugin;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.MutableDate;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;

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
    public void onPageLoad() {
        onPrepare();
        onRefresh();
        onClearForm();
    }

    public void onPrepare() {
        getModel().setDepartments(Vr.get().entitySelectItemsNullable("AppDepartment"));
        getModel().setEquipmentTypes(Vr.get().entitySelectItemsNullable("EquipmentType"));
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

    public void onRefresh() {
        CorePlugin core = CorePlugin.get();
        EquipmentPlugin eq = EquipmentPlugin.get();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        List<EquipmentForResponsibleInfo> bb = new ArrayList<>();
        if (getModel().getFilterEquipmentType() != null) {
            bb = ebs.findBorrowableEquipmentsForResponsibleInfo(core.getCurrentUserIdFF(),
                    getModel().getFilterEquipmentType(),
                    getModel().getFilterDepartment()
            );
        }
        getModel().setEquipments(bb);
        getModel().setVisaUsers(Vr.get().toEntitySelectItemsNullable(ebs.findBorrowVisaUsers(), "AppUser"));
        Integer currentUserId = core.getCurrentUserId();
        
        getModel().setSuperUserOperator(ebs.isBorrowSuperOperatorUser(currentUserId));
        
        if (getModel().isSuperUserOperator()
                || ebs.isBorrowSuperOperatorUser(currentUserId)) {
            getModel().setBorrowUsers(Vr.get().toEntitySelectItemsNullable(core.findUsers(), "AppUser"));
            getModel().setDelegatedBorrow(true);
        } else {
            getModel().setBorrowUsers(new ArrayList<>());
            getModel().setDelegatedBorrow(false);
        }
        onClearForm();
    }

    public void onClearForm() {
        getModel().setFromDate(new MutableDate().setTime(0, 0, 0, 0).getDateTime());
        getModel().setToDate(null);
        getModel().setVisaUser(null);
        getModel().setBorrowUser(null);
        getModel().setQuantity(1.0);
        getModel().setSelectedEquipment(null);
    }

    public void onAddRequest(boolean deliver) {
        CorePlugin core = CorePlugin.get();
        EquipmentBorrowRequest req = new EquipmentBorrowRequest();
        AppUser bu = core.findUser(getModel().getBorrowUser());
        if (bu == null) {
            bu = core.getCurrentUser();
        }
        req.setBorrowerUser(bu);
        req.setEquipment(getModel().getSelectedEquipment() == null ? null : getModel().getSelectedEquipment().getEquipment());
        req.setFromDate(getModel().getFromDate());
        req.setToDate(getModel().getToDate());
        req.setVisaUser(core.findUser(getModel().getVisaUser()));
        req.setQuantity(getModel().getQuantity());
        try {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            ebs.addEquipmentBorrowRequest(req);
            if (getModel().getBorrowUser() != null && getModel().isDelegatedBorrow()) {
                if (getModel().isSuperUserOperator()) {
                    ebs.applyEquipmentRequestByVisaUser(req.getId(), EquipmentBorrowOperatorType.SUPER_OPERATOR, null, true, deliver);
                } else {
                    ebs.applyEquipmentRequestByVisaUser(req.getId(), EquipmentBorrowOperatorType.OPERATOR, null, true, deliver);
                }
            }
            EquipmentForResponsibleInfo selectedEquipment = getModel().getSelectedEquipment();
            onClearForm();
            if (getModel().getBorrowUser() != null) {
                FacesUtils.addInfoMessage("Emprunt " + (selectedEquipment.getName()) + " réussi");
            } else {
                FacesUtils.addInfoMessage("Reservation " + (selectedEquipment.getName()) + " réussie");
            }
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
        private List<SelectItem> equipmentTypes;
        private List<SelectItem> departments;
        private Integer filterEquipmentType;
        private Integer filterDepartment;
        private EquipmentForResponsibleInfo selectedEquipment;
        private Date fromDate;
        private Date toDate;
        private double quantity;
        private Integer visaUser;
        private Integer borrowUser;
        private List<SelectItem> visaUsers;
        private List<SelectItem> borrowUsers;
        private boolean delegatedBorrow;
        private boolean superUserOperator;

        public boolean isDelegatedBorrow() {
            return delegatedBorrow;
        }

        public void setDelegatedBorrow(boolean delegatedBorrow) {
            this.delegatedBorrow = delegatedBorrow;
        }

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

        public Integer getVisaUser() {
            return visaUser;
        }

        public void setVisaUser(Integer visaUser) {
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

        public Integer getFilterEquipmentType() {
            return filterEquipmentType;
        }

        public void setFilterEquipmentType(Integer filterEquipmentType) {
            this.filterEquipmentType = filterEquipmentType;
        }

        public Integer getFilterDepartment() {
            return filterDepartment;
        }

        public void setFilterDepartment(Integer filterDepartment) {
            this.filterDepartment = filterDepartment;
        }

        public List<SelectItem> getBorrowUsers() {
            return borrowUsers;
        }

        public void setBorrowUsers(List<SelectItem> borrowUsers) {
            this.borrowUsers = borrowUsers;
        }

        public Integer getBorrowUser() {
            return borrowUser;
        }

        public void setBorrowUser(Integer borrowUser) {
            this.borrowUser = borrowUser;
        }

        public List<SelectItem> getEquipmentTypes() {
            return equipmentTypes;
        }

        public void setEquipmentTypes(List<SelectItem> equipmentTypes) {
            this.equipmentTypes = equipmentTypes;
        }

        public List<SelectItem> getDepartments() {
            return departments;
        }

        public void setDepartments(List<SelectItem> departments) {
            this.departments = departments;
        }

        public boolean isSuperUserOperator() {
            return superUserOperator;
        }

        public void setSuperUserOperator(boolean superUserOperator) {
            this.superUserOperator = superUserOperator;
        }

    }
}
