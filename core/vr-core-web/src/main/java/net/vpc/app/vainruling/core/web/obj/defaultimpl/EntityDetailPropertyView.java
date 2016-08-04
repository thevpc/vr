/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.obj.ObjManagerService;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewManager;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.Relationship;
import net.vpc.upa.RelationshipRole;

/**
 * @author taha.bensalah@gmail.com
 */
public class EntityDetailPropertyView extends PropertyView {

    private String actionCommand;
    private Relationship relation;
    private long count;

    public EntityDetailPropertyView(String componentId, Relationship relation, String ctrlType, PropertyViewManager manager) {
        super(componentId, resolveLabel(relation.getSourceRole()), relation, ctrlType, manager);
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

    private static String resolveLabel(RelationshipRole rols) {
        String orNull = VrApp.getBean(I18n.class).getOrNull(rols);
        if (orNull == null) {
            orNull = VrApp.getBean(I18n.class).getOrNull("Entity." + rols.getEntity().getName() + ".ListTitle");
            if (orNull == null) {
                orNull = VrApp.getBean(I18n.class).getOrNull(rols.getEntity());
            }
        }
        if (orNull == null) {
            orNull = VrApp.getBean(I18n.class).get(rols);
        }
        return orNull;
    }

    @Override
    public void refresh() {
        Field f = relation.getSourceRole().getEntityField();
        if (f == null) {
            f = relation.getSourceRole().getFields().get(0);
        }
        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
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
        count = objService.findCountByFilter(relation.getSourceEntity().getName(), idExpr + "=" + idVal, null);
    }

    public long getCount() {
        return count;
    }

    public String buildActionCommand() {
        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
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

    public Entity getTargetEntity() {
        return getRelationship().getTargetEntity();
    }

    public Entity getSourceEntity() {
        return getRelationship().getSourceEntity();
    }
}
