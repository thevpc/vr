/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.obj.defaultimpl;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.util.I18n;
import net.vpc.app.vainruling.core.web.obj.ObjCtrl;
import net.vpc.app.vainruling.core.web.obj.PropertyView;
import net.vpc.app.vainruling.core.web.obj.PropertyViewManager;
import net.vpc.upa.*;
import net.vpc.upa.config.ManyToOne;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.ManyToOneType;

import java.util.HashMap;
import java.util.List;

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
        List<Field> sfields = ((ManyToOneType) f.getDataType()).getRelationship().getSourceRole().getFields();
//        Field f3=f;
//        if (f3.getDataType() instanceof ManyToOneType) {
//            List<Field> fields2 = ((ManyToOneType) f3.getDataType()).getRelationship().getSourceRole().getFields();
//            if (fields2.size() == 1) {
//                f3 = fields2.get(0);
//            }
//        }


        List<PrimitiveField> idPrimitiveFields = relation.getSourceRole().getEntity().getIdPrimitiveFields();
        StringBuilder idValues=new StringBuilder();
        StringBuilder idExpr=new StringBuilder("(");
        for (int i = 0; i < idPrimitiveFields.size(); i++) {
            if(i>0){
                idExpr.append(" AND ");
            }
            idExpr.append("o.`").append(sfields.get(i).getName()).append("`=${ID").append(i).append("}");
//            idExpr.append("o.`").append(f.getName()).append("`.`").append(idPrimitiveFields.get(i).getName()).append("`=${ID").append(i).append("}");
            if(i>0){
                idValues.append(",");
            }
            idValues.append(sfields.get(i).getName()).append(":${ID").append(i).append("}");
        }
        idExpr.append(")");

        String argEntity = "entity:\"" + relation.getSourceEntity().getName() + "\"";
        String argListFilter = "listFilter:\"" + idExpr + "\"";
        String argValues = "values:{" + idValues + "}";
        String argDisabledFields = "disabledFields:[\"" + f.getName() + "\"]";
        String argIgnoreAutoFilter = "ignoreAutoFilter:true";

        setActionCommand("{" + argEntity + "," + argListFilter + "," + argValues + "," + argDisabledFields + "," + argIgnoreAutoFilter + "}");
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
        PrimitiveId primitiveId = (PrimitiveId) resolveId();
        HashMap<String, Object> parameters = new HashMap<>();
        if(primitiveId!=null) {
            StringBuilder idExpr=new StringBuilder("(");
            for (int i = 0; i < primitiveId.size(); i++) {
                if(i>0){
                    idExpr.append(" AND ");
                }
                idExpr.append("o.`").append(f.getName()).append("`.`").append(primitiveId.getField(i).getName()).append("`=:idval").append(i);
                parameters.put("idval"+i, primitiveId.getValue(0));
            }
            idExpr.append(")");
            CorePlugin core = VrApp.getBean(CorePlugin.class);
            count = core.findCountByFilter(relation.getSourceEntity().getName(), idExpr.toString(), null, parameters);
        }else{
            count=0;
        }
    }

    public long getCount() {
        return count;
    }

    public PrimitiveId resolveId() {
        Entity targetEntity = getRelationship().getTargetEntity();
        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
        Document currentDocument = ctrl.getModel().getCurrentDocument();
        return targetEntity.getBuilder().objectToPrimitiveId(currentDocument);
    }

    public String buildActionCommand() {
//        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
        PrimitiveId idVal2 = resolveId();
        String cmd=getActionCommand();
        List<PrimitiveField> idPrimitiveFields = relation.getSourceRole().getEntity().getIdPrimitiveFields();
        for (int i = 0; i < idPrimitiveFields.size(); i++) {
//            PrimitiveField o = idPrimitiveFields.get(i);
            Object idVal=idVal2==null?null:idVal2.getValue(i);
            if (idVal == null) {
                idVal = "null";
            } else if (idVal instanceof Number) {
                idVal = idVal.toString();
            } else if (idVal instanceof String){
                idVal = "'" + idVal.toString().replace("'", "''") + "'";
            }else{
                throw new IllegalArgumentException("Not Supported yet");
            }

            cmd = cmd.replace("${ID"+i+"}", idVal == null ? "" : idVal.toString());
        }
        return cmd;
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
