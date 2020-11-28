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
import net.thevpc.app.vainruling.plugins.equipments.borrow.model.info.EquipmentForResponsibleInfo;
import net.thevpc.app.vainruling.plugins.equipments.borrow.service.EquipmentBorrowService;
import net.thevpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginSecurity;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.VrPage;

//import javax.annotation.PostConstruct;
/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Site", css = "fa-dashboard", ctrl = "")},
        menu = "/Equipment",
        url = "modules/equipments/my-borrowed-equipments",
        securityKey = EquipmentPluginSecurity.RIGHT_CUSTOM_EQUIPMENT_BORROWED
)
@ManagedBean
public class MyBorrowedEquipmentsCtrl {

    private final Model model = new Model();

    public Model getModel() {
        return model;
    }

    public boolean isCancelEnabled(EquipmentForResponsibleInfo r) {
        return r.isCancelEnabled();
    }

    public boolean isArchiveEnabled(EquipmentForResponsibleInfo r) {
        return r.isArchiveEnabled();
    }

    public void onCancel(EquipmentForResponsibleInfo r) {
        if (r.getRequest() != null) {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            ebs.cancelRequest(r.getRequest().getId(), null);
            onRefresh();
        }
    }

    public void onArchive(EquipmentForResponsibleInfo r) {
        if (r.getRequest() != null) {
            EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
            ebs.archiveRequest(r.getRequest().getId(), null);
            onRefresh();
        }
    }

    @VrOnPageLoad
    public void onRefresh() {
        EquipmentBorrowService ebs = VrApp.getBean(EquipmentBorrowService.class);
        getModel().setEquipments(ebs.findBorrowedEquipmentsForResponsibleInfo(CorePlugin.get().getCurrentUserIdFF(), null, null));
    }

    public static class Model {

        private List<EquipmentForResponsibleInfo> equipments;

        public List<EquipmentForResponsibleInfo> getEquipments() {
            return equipments;
        }

        public void setEquipments(List<EquipmentForResponsibleInfo> equipments) {
            this.equipments = equipments;
        }

    }
}
