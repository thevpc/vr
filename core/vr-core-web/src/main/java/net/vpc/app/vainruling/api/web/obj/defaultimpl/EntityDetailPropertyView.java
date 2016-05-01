/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj.defaultimpl;

import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.core.ObjManagerService;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.web.obj.ObjCtrl;
import net.vpc.app.vainruling.api.web.obj.PropertyView;
import net.vpc.app.vainruling.api.web.obj.PropertyViewManager;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.Relationship;

/**
 *
 * @author vpc
 */
public class EntityDetailPropertyView extends PropertyView {

    private String actionCommand;
    private Relationship relation;

    public EntityDetailPropertyView(String componentId, Relationship relation, String ctrlType, PropertyViewManager manager) {
        super(componentId, VrApp.getBean(I18n.class).get(relation.getSourceRole()), relation, ctrlType, manager);
        setHeader(VrApp.getBean(I18n.class).get(relation.getSourceRole()));
        setDataType(relation.getDataType());
        setPrependNewLine(true);
        setColspan(Integer.MAX_VALUE);
        setAppendNewLine(true);
        this.relation = relation;
        Field f = relation.getSourceRole().getEntityField();
        if (f == null) {
            f = relation.getSourceRole().getFields().get(0);
        }
        String idExpr = "o.`" + f.getName() + "`.`" + relation.getTargetEntity().getPrimaryFields().get(0).getName() + "`";

        String argEntity = "entity:\"" + relation.getSourceEntity().getName() + "\"";
        String argListFilter = "listFilter:\"" + idExpr + "=${ID}\"";
        String argValues = "values:{" + f.getName() + ":${ID}}";
        String argDisabledFields = "disabledFields:[\"" + f.getName() + "\"]";

        setActionCommand("{" + argEntity + "," + argListFilter + "," + argValues + "," + argDisabledFields + "}");
    }

    public long evalCount() {
        Field f = relation.getSourceRole().getEntityField();
        if (f == null) {
            f = relation.getSourceRole().getFields().get(0);
        }
        ObjCtrl ctrl = (ObjCtrl) VrApp.getBean(ObjCtrl.class);
        String idExpr = "o.`" + f.getName() + "`.`" + relation.getTargetEntity().getPrimaryFields().get(0).getName() + "`";
        Object idVal = getRelationship().getTargetEntity().getBuilder().recordToId(ctrl.getModel().getCurrentRecord());
        if (idVal == null) {
            idVal = "null";
        } else if (idVal instanceof Number) {
            idVal = idVal.toString();
        } else {
            idVal = "'" + idVal.toString().replace("'", "''") + "'";
        }

        ObjManagerService objService = VrApp.getBean(ObjManagerService.class);
        return objService.findCountByFilter(relation.getSourceEntity().getName(), idExpr + "=" + idVal, null);
    }

    public String buildActionCommand() {
        ObjCtrl ctrl = (ObjCtrl) VrApp.getBean(ObjCtrl.class);
        Object idVal = getRelationship().getTargetEntity().getBuilder().recordToId(ctrl.getModel().getCurrentRecord());
        if (idVal == null) {
            idVal = "null";
        } else if (idVal instanceof Number) {
            idVal = idVal.toString();
        } else {
            idVal = "'" + idVal.toString().replace("'", "''") + "'";
        }
        return getActionCommand().replace("${ID}", idVal == null ? "" : idVal.toString());
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public final void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public Relationship getRelationship() {
        return (Relationship) getReferrer();
    }

    public Entity getMasterEntity() {
        return getRelationship().getTargetRole().getEntity();
    }

    public Entity getDetailEntity() {
        return getRelationship().getSourceRole().getEntity();
    }
}
