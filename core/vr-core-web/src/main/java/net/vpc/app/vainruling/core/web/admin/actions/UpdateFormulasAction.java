/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.admin.actions;

import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.obj.EntityAction;
import net.vpc.app.vainruling.core.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.core.web.obj.ActionDialog;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.upa.UPA;

import java.util.List;

/**
 * @author vpc
 */
@EntityAction(entityType = AppConfig.class,
        actionLabel = "MAJ Formules", actionStyle = "fa-calculator",
        dialog = false
)
public class UpdateFormulasAction implements ActionDialog {

    @Override
    public void openDialog(String actionId, List<String> itemIds) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public boolean isEnabled(Class entityType, EditCtrlMode mode, Object value) {
        return true;//value != null;
    }

    @Override
    public void invoke(Class entityType, Object obj, Object[] args) {
        UPA.getPersistenceUnit().updateFormulas();
        FacesUtils.addInfoMessage("Mise à jour réussie");
    }
}
