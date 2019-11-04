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
import net.vpc.app.vainruling.core.service.model.strict.AppUserStrict;
import net.vpc.app.vainruling.core.web.jsf.Vr;
import net.vpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowRequest;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import net.vpc.common.jsf.FacesUtils;
import org.primefaces.event.SelectEvent;
import net.vpc.app.vainruling.VrPage;
import net.vpc.app.vainruling.VrPathItem;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.EquipmentBorrowWorkflow;
import net.vpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentBorrowOperatorType;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.MutableDate;
import net.vpc.common.util.Tuple2;
import net.vpc.app.vainruling.VrOnPageLoad;

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

    @VrOnPageLoad
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
            return "<sélectionner équipement dans la liste>";
        }
        if (e.getEquipment() == null) {
            return "<sélectionner équipement dans la liste>";
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

    public List<SelectItem> _AppUserStrict_toSelectItems(List<AppUserStrict> a) {
        return Vr.get().toSelectItems(a, x -> new Tuple2<>(String.valueOf(x.getId()), x.getFullTitle()));
    }

    public void onRefresh() {
        CorePlugin core = CorePlugin.get();
        onRefreshEquipements();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        getModel().setVisaUsers(_AppUserStrict_toSelectItems(ebs.findBorrowVisaUsers()));
        Integer currentUserId = core.getCurrentUserId();
        getModel().setSuperUserOperator(ebs.isBorrowSuperOperatorUser(currentUserId));

        if (getModel().isSuperUserOperator()
                || ebs.isBorrowOperatorUser(currentUserId)) {
            getModel().setBorrowUsers(_AppUserStrict_toSelectItems(ebs.findBorrowUsers()));
            getModel().setDelegatedBorrow(true);
        } else {
            getModel().setBorrowUsers(new ArrayList<>());
            getModel().setDelegatedBorrow(false);
        }
        onClearForm();
    }

    public void onRefreshEquipements() {
        CorePlugin core = CorePlugin.get();
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        List<EquipmentForResponsibleInfo> bb = new ArrayList<>();
        if (getModel().getFilterEquipmentType() != null || StringUtils.trim(getModel().getSearchText()).length()>=2) {
            bb = ebs.findBorrowableEquipmentsForResponsibleInfo(core.getCurrentUserIdFF(),
                    getModel().getFilterEquipmentType(),
                    getModel().getFilterDepartment(),
                    getModel().getSearchText()
            );
        }
        getModel().setEquipments(bb);
    }

    public void onClearForm() {
        getModel().setFromDate(new MutableDate().setTime(0, 0, 0, 0).getDateTime());
        getModel().setToDate(null);
        getModel().setVisaUser(null);
        getModel().setBorrowUser(null);
        getModel().setQuantity(1.0);
        getModel().setSelectedEquipment(null);
        EquipmentForResponsibleInfo se = getModel().getSelectedEquipment();
        if (se == null) {
            getModel().setRequireVisaUser(false);
        } else {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            EquipmentBorrowWorkflow e = ebs.resolveBorrowWorkflow(se.getEquipment());
            getModel().setRequireVisaUser(e.isRequireUser());
        }
    }

    public void onAddRequest(boolean deliver) {
        try {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            Integer ii = getModel().getSelectedEquipment() == null ? -1 : getModel().getSelectedEquipment().getEquipment().getId();
            EquipmentBorrowRequest req = ebs.addEquipmentBorrowRequest(getModel().getBorrowUser(), ii,
                    getModel().getFromDate(), getModel().getToDate(), getModel().getVisaUser(), getModel().getQuantity(),
                    getModel().isDelegatedBorrow()
            );
            if (getModel().getBorrowUser() != null && getModel().isDelegatedBorrow()) {
                if (getModel().isSuperUserOperator()) {
                    ebs.applyVisa(req.getId(), EquipmentBorrowOperatorType.SUPER_OPERATOR, null, true, deliver);
                } else {
                    ebs.applyVisa(req.getId(), EquipmentBorrowOperatorType.OPERATOR, null, true, deliver);
                }
            }
            EquipmentForResponsibleInfo selectedEquipment = getModel().getSelectedEquipment();
            onRefreshEquipements();
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
        Object se2 = event.getObject();
        EquipmentForResponsibleInfo se = null;
        if (se2 instanceof EquipmentForResponsibleInfo) {
            se = (EquipmentForResponsibleInfo) se2;
        } else {
            se = getModel().getSelectedEquipment();
        }
        if (se == null) {
            getModel().setRequireVisaUser(false);
        } else {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            EquipmentBorrowWorkflow e = ebs.resolveBorrowWorkflow(se.getEquipment());
            getModel().setRequireVisaUser(e.isRequireUser());
        }
    }

    public static class Model {

        private List<EquipmentForResponsibleInfo> equipments;
        private List<SelectItem> equipmentTypes;
        private List<SelectItem> departments;
        private String searchTextHelper = "Tapez ici les mots clés de recherche.";
        private String searchText;
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
        private boolean requireVisaUser;
        private boolean delegatedBorrow;
        private boolean superUserOperator;

        public boolean isRequireVisaUser() {
            return requireVisaUser;
        }

        public void setRequireVisaUser(boolean requireVisaUser) {
            this.requireVisaUser = requireVisaUser;
        }

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

        public String getSearchTextHelper() {
            return searchTextHelper;
        }

        public void setSearchTextHelper(String searchTextHelper) {
            this.searchTextHelper = searchTextHelper;
        }

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

    }
}
