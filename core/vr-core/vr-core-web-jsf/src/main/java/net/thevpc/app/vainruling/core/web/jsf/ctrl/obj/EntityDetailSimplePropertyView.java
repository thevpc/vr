/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl.obj;

import java.util.Collections;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.EditorCtrl;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.common.collections.CollectionDiff;
import net.thevpc.upa.*;
import net.thevpc.upa.types.ManyToOneType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.thevpc.app.vainruling.core.web.jsf.VrJsf;

/**
 * @author taha.bensalah@gmail.com
 */
public class EntityDetailSimplePropertyView extends PropertyView {

    private String actionCommand;
    private Relationship relation;
    private Relationship pivotRelation;
    private String pivotField;
    private long count;
    Entity detailEntity;
    Entity pivotEntity;
    Entity masterEntity;

    public EntityDetailSimplePropertyView(String componentId, Relationship relation, String ctrlType, String fieldToList, PropertyViewManager manager) {
        super(componentId, resolveLabel(relation.getSourceRole()), relation, ctrlType, manager);
        setHeader(relation.getSourceRole().getTitle());
        setDataType(relation.getDataType());
        setPrependNewLine(true);
        setColspan(Integer.MAX_VALUE);
        setAppendNewLine(true);
        this.relation = relation;
        Field fieldToListObj = relation.getSourceEntity().getField(fieldToList);
        pivotRelation = ((ManyToOneType) fieldToListObj.getDataType()).getRelationship();
        detailEntity = relation.getSourceEntity();
        pivotEntity = pivotRelation.getTargetEntity();
        masterEntity = relation.getTargetEntity();

        this.pivotField = fieldToList;
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
        StringBuilder idValues = new StringBuilder();
        StringBuilder idExpr = new StringBuilder("(");
        for (int i = 0; i < idPrimitiveFields.size(); i++) {
            if (i > 0) {
                idExpr.append(" AND ");
            }
            idExpr.append("o.`").append(sfields.get(i).getName()).append("`=${ID").append(i).append("}");
//            idExpr.append("o.`").append(f.getName()).append("`.`").append(idPrimitiveFields.get(i).getName()).append("`=${ID").append(i).append("}");
            if (i > 0) {
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
            Entity entity = rols.getEntity();
            Map<String, Object> args = new HashMap<>();
            String entityPlainTitle = entity.getTitle();
            args.put("title", entityPlainTitle);
            args.put("name", entity.getName());
            orNull = VrApp.getBean(I18n.class).getOrNull("Entity." + entity.getName() + ".ListTitle");
            if (orNull == null) {
                orNull = VrApp.getBean(I18n.class).getOrNull(entity);
            }
        }
        if (orNull == null) {
            orNull = rols.getTitle();
        }
        return orNull;
    }

    @Override
    public void refresh() {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        List<NamedId> sourceFilterAllPossibilities = core.findAllNamedIds(pivotRelation.getTargetEntity().getName(), null, null, null, null);
        setItems(VrJsf.toSelectItemList(sourceFilterAllPossibilities));

        Field f = relation.getSourceRole().getEntityField();
        if (f == null) {
            f = relation.getSourceRole().getFields().get(0);
        }
        PrimitiveId primitiveId = (PrimitiveId) resolveId();
        HashMap<String, Object> parameters = new HashMap<>();
        if (primitiveId != null) {
            StringBuilder idExpr = new StringBuilder("(");
            for (int i = 0; i < primitiveId.size(); i++) {
                if (i > 0) {
                    idExpr.append(" AND ");
                }
                idExpr.append("o.`").append(f.getName()).append("`.`").append(primitiveId.getField(i).getName()).append("`=:idval").append(i);
                parameters.put("idval" + i, primitiveId.getValue(0));
            }
            idExpr.append(")");
            List<Document> z = core.findAllDocuments(relation.getSourceEntity().getName(), new String[]{pivotField}, idExpr.toString(), null, null, parameters);
            EntityBuilder bb = relation.getSourceEntity().getBuilder();
            setSelectedItems(z.stream().map(x -> String.valueOf(bb.documentToId(x))).collect(Collectors.toList()));
            count = core.findCountByFilter(relation.getSourceEntity().getName(), idExpr.toString(), null, null, parameters);
        } else {
            setSelectedItems(Collections.emptyList());
            count = 0;
        }
    }

    public void onPostUpdate(Entity entity, Document document) {
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        Field f = relation.getSourceRole().getEntityField();
        if (f == null) {
            f = relation.getSourceRole().getFields().get(0);
        }
        PrimitiveId primitiveId = (PrimitiveId) resolveId();
        HashMap<String, Object> parameters = new HashMap<>();
        if (primitiveId != null) {
            StringBuilder idExpr = new StringBuilder("(");
            for (int i = 0; i < primitiveId.size(); i++) {
                if (i > 0) {
                    idExpr.append(" AND ");
                }
                idExpr.append("o.`").append(f.getName()).append("`.`").append(primitiveId.getField(i).getName()).append("`=:idval").append(i);
                parameters.put("idval" + i, primitiveId.getValue(0));
            }
            idExpr.append(")");
            List<Document> olddocs = core.findAllDocuments(detailEntity.getName(), null, idExpr.toString(), null, null, parameters);
//            List<Document> oldSelected = core.findAllDocuments(relation.getSourceEntity().getName(), new String[]{fieldToList}, idExpr.toString(), null, null, parameters);
            EntityBuilder bb2 = pivotEntity.getBuilder();
            Map<String, Document> fieldToMap
                    = (olddocs.stream()
                            .collect(Collectors.toMap(x -> String.valueOf(bb2.documentToId(x.get(pivotField))), java.util.function.Function.identity())));
            CollectionDiff<String> diff = CollectionDiff.of(fieldToMap.keySet(), getSelectedItems());
            EntityBuilder bb = detailEntity.getBuilder();
            for (String id : diff.getRemoved()) {
                Document doc = fieldToMap.get(id);
                if (doc != null) {
                    Object id2 = bb.documentToId(doc);
                    detailEntity.remove(RemoveOptions.forId(id2));
                }
            }
            for (Object id : diff.getAdded().stream().map(x -> core.parseEntityId(pivotEntity.getName(), x)).toArray()) {
                Object eid = relation.getTargetEntity().getBuilder().primitiveIdToId(primitiveId);
                Object e = relation.getTargetEntity().findById(eid);
                Document d2 = relation.getSourceEntity().getBuilder().createDocument();
                d2.set(relation.getSourceRole().getEntityField().getName(), e);
                d2.set(pivotField, pivotEntity.findById(id));
                core.save(detailEntity.getName(), d2);
            }
        } else {
            setSelectedItems(Collections.emptyList());
            count = 0;
        }
    }

    public void onPostPersist(Entity entity, Document c) {

    }

    public void setSelectedItem(Object selectedItem) {
        super.setSelectedItem(selectedItem);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    public long getCount() {
        return count;
    }

    public PrimitiveId resolveId() {
        Entity targetEntity = getRelationship().getTargetEntity();
        EditorCtrl ctrl = VrApp.getBean(EditorCtrl.class);
        Document currentDocument = ctrl.getModel().getCurrentDocument();
        return targetEntity.getBuilder().objectToPrimitiveId(currentDocument);
    }

    public String buildActionCommand() {
//        EditorCtrl ctrl = VrApp.getBean(EditorCtrl.class);
        PrimitiveId idVal2 = resolveId();
        String cmd = getActionCommand();
        List<PrimitiveField> idPrimitiveFields = relation.getSourceRole().getEntity().getIdPrimitiveFields();
        for (int i = 0; i < idPrimitiveFields.size(); i++) {
//            PrimitiveField o = idPrimitiveFields.get(i);
            Object idVal = idVal2 == null ? null : idVal2.getValue(i);
            if (idVal == null) {
                idVal = "null";
            } else if (idVal instanceof Number) {
                idVal = idVal.toString();
            } else if (idVal instanceof String) {
                idVal = "'" + idVal.toString().replace("'", "''") + "'";
            } else {
                throw new IllegalArgumentException("Not Supported yet");
            }

            cmd = cmd.replace("${ID" + i + "}", idVal == null ? "" : idVal.toString());
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
