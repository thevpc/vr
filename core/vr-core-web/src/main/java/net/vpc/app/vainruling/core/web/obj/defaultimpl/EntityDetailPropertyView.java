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
        Field f2 = relation.getTargetEntity().getIdFields().get(0);
        if (f2.getDataType() instanceof ManyToOneType) {
            List<Field> fields2 = ((ManyToOneType) f2.getDataType()).getRelationship().getSourceRole().getFields();
            if (fields2.size() == 1) {
                f2 = fields2.get(0);
            }
        }
        String idExpr = "o.`" + f.getName() + "`.`" + f2.getName() + "`";

        String argEntity = "entity:\"" + relation.getSourceEntity().getName() + "\"";
        String argListFilter = "listFilter:\"" + idExpr + "=${ID}\"";
        Field f3=f;
        if (f3.getDataType() instanceof ManyToOneType) {
            List<Field> fields2 = ((ManyToOneType) f3.getDataType()).getRelationship().getSourceRole().getFields();
            if (fields2.size() == 1) {
                f3 = fields2.get(0);
            }
        }
        String argValues = "values:{" + f3.getName() + ":${ID}}";
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
        Field f2 = relation.getTargetEntity().getIdFields().get(0);
        if (f2.getDataType() instanceof ManyToOneType) {
            List<Field> fields2 = ((ManyToOneType) f2.getDataType()).getRelationship().getSourceRole().getFields();
            if (fields2.size() == 1) {
                f2 = fields2.get(0);
            }
        }
        String idExpr = "o.`" + f.getName() + "`.`" + f2.getName() + "`";
        Object idVal = resolveId();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("idval", idVal);

        CorePlugin core = VrApp.getBean(CorePlugin.class);
        count = core.findCountByFilter(relation.getSourceEntity().getName(), idExpr + "=:idval", null, parameters);
    }

    public long getCount() {
        return count;
    }

    public Object resolveId() {
        Entity targetEntity = getRelationship().getTargetEntity();
        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
        Document currentDocument = ctrl.getModel().getCurrentDocument();
        Object idVal = null;
        boolean cont = true;
        while (cont) {
            cont = false;
            idVal = targetEntity.getBuilder().documentToId(currentDocument);
            if (idVal != null) {
                List<Field> idFields = targetEntity.getIdFields();
                if (idFields.size() == 1 && idFields.get(0).getDataType() instanceof ManyToOneType) {
                    if (idVal instanceof Document || targetEntity.getEntityType().isInstance(idVal)) {
                        targetEntity = ((ManyToOneType) idFields.get(0).getDataType()).getTargetEntity();
                        if (idVal instanceof Document) {
                            currentDocument = (Document) idVal;
                        } else {
                            currentDocument = targetEntity.getBuilder().objectToDocument(idVal);
                        }
                        cont=true;
                    }
                }
            }
        }
        return idVal;
    }

    public static Object resolveId(Object value,DataType type) {
        if(value==null){
            return null;
        }
        if(type instanceof ManyToOneType){
            Entity targetEntity = ((ManyToOneType) type).getTargetEntity();
            if(targetEntity.getIdFields().size()==1) {
                return resolveId(targetEntity.getBuilder().objectToId(value), targetEntity.getIdFields().get(0).getDataType());
            }
        }
        return value;
    }

    public String buildActionCommand() {
        ObjCtrl ctrl = VrApp.getBean(ObjCtrl.class);
        Object idVal = resolveId();
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
