/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.vpc.app.vainruling.api.ui.UIConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;
import net.vpc.app.vainruling.api.core.ActionInfo;
import net.vpc.app.vainruling.api.core.ObjManagerService;
import net.vpc.app.vainruling.api.i18n.I18n;
import net.vpc.app.vainruling.api.util.VrHelper;
import net.vpc.app.vainruling.api.web.BreadcrumbItem;
import net.vpc.app.vainruling.api.web.OnPageLoad;
import net.vpc.app.vainruling.api.web.UCtrl;
import net.vpc.app.vainruling.api.web.UCtrlData;
import net.vpc.app.vainruling.api.web.UCtrlProvider;
import net.vpc.app.vainruling.api.web.UPathItem;
import net.vpc.app.vainruling.api.web.VrMenuManager;
import net.vpc.app.vainruling.api.web.ctrl.AbstractObjectCtrl;
import net.vpc.app.vainruling.api.web.ctrl.EditCtrlMode;
import net.vpc.app.vainruling.api.web.obj.defaultimpl.EntityDetailPropertyView;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.utils.Convert;
import net.vpc.common.utils.PlatformTypes;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.Entity;
import net.vpc.upa.EntityBuilder;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.Package;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Relationship;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UPA;
import net.vpc.upa.UPASecurityManager;
import net.vpc.upa.filters.Fields;
import net.vpc.upa.types.DataType;
import net.vpc.upa.types.EntityType;
import net.vpc.upa.types.EnumType;
import net.vpc.upa.types.IntType;
import net.vpc.upa.types.StringType;
import net.vpc.upa.types.TemporalType;
import org.primefaces.event.SelectEvent;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author vpc
 */
@UCtrl(
        breadcrumb = {
            @UPathItem(title = "Entité", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Liste Entités",
        url = "modules/obj/objects"
)
@ManagedBean
@Scope(value = "session")
public class ObjCtrl extends AbstractObjectCtrl<ObjRow> implements UCtrlProvider {

    @Autowired
    private ObjManagerService objService;
    @Autowired
    private PropertyViewManager propertyViewManager;
    @Autowired
    private I18n i18n;
    private final List<ColumnView> columns = new ArrayList<>();
    private final List<PropertyView> properties = new ArrayList<>();
    private final Map<String, Boolean> enabledButtons = new HashMap<>();
    private DynaFormModel dynModel = new DynaFormModel();
    private static final Logger log = Logger.getLogger(ObjCtrl.class.getName());

    public ObjCtrl() {
        super(null);
        this.model = new PModel();
    }

    @Override
    public UCtrlData getUCtrl(String cmd) {
        try {
            Config c = VrHelper.parseJSONObject(cmd, Config.class);
            Entity entity = UPA.getPersistenceUnit().getEntity(c.entity);
            UCtrlData d = new UCtrlData();
            d.setTitle(getPageTitleString(entity, EditCtrlMode.LIST));
            d.setUrl("modules/obj/objects");
            d.setCss("fa-table");
            List<BreadcrumbItem> items = new ArrayList<>();

            Package p = entity.getParent();
            int pos = items.size();
            while (p != null && !"/".equals(p.getPath())) {
                items.add(pos, new BreadcrumbItem(i18n.get(p), "fa-dashboard", "", ""));
                p = p.getParent();
            }
            d.setBreadcrumb(items.toArray(new BreadcrumbItem[items.size()]));
            return d;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
        }
        return null;
    }

    public String getPageTitleString(Entity entity, EditCtrlMode mode) {
        if (mode == null) {
            mode = EditCtrlMode.LIST;
        }
        if (entity == null) {
            switch (mode) {
                case LIST: {
                    return ("Liste Objets");
                }
                case UPDATE: {
                    return ("Mise à jour Objet");
                }
                case NEW: {
                    return ("Nouvel Objet");
                }
            }
            return ("???");
        }
        String newTitleString = "???";
        String entityPlainTitle = i18n.get(entity);
        switch (mode) {
            case LIST: {
                newTitleString = i18n.getOrNull(entity.getI18NString().append("ListTitle"), entityPlainTitle);
                if (newTitleString == null) {
                    newTitleString = "Liste " + entityPlainTitle;
                }
                break;
            }
            case UPDATE: {
                newTitleString = i18n.getOrNull(entity.getI18NString().append("UpdateTitle"), entityPlainTitle);
                if (newTitleString == null) {
                    newTitleString = "Mise à jour " + entityPlainTitle;
                }
                break;
            }
            case NEW: {
                newTitleString = i18n.getOrNull(getEntity().getI18NString().append("NewTitle"), entityPlainTitle);
                if (newTitleString == null) {
                    newTitleString = "Nouveau " + entityPlainTitle;
                }
                break;
            }
        }
        return newTitleString;
    }

    public boolean isEnabledButton(String buttonId) {
        Boolean v = enabledButtons.get(buttonId);
        if (v == null) {
            v = isEnabledButtonImpl(buttonId);
            enabledButtons.put(buttonId, v);
        }
        return v.booleanValue();
    }

    public boolean isEnabledButtonImpl(String buttonId) {
        try {
            switch (getModel().getMode()) {
                case LIST: {
                    if ("Select".equals(buttonId)) {
                        return isEnabledButton("Remove")
                                || isEnabledButton("Archive");
                    }
                    if ("Refresh".equals(buttonId)) {
                        return true;
                    }
                    if ("New".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedPersist(getEntity());
                    }
                    if ("Cancel".equals(buttonId)) {
                        return false;
                    }
                    if ("Remove".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedRemove(getEntity());
                    }
                    if ("Archive".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager()
                                .isAllowedUpdate(getEntity())
                                && objService.isArchivable(getModel().getEntityName());
                    }
                    if (objService.isEntityAction(getEntity().getName(), buttonId, null)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedKey(getEntity(), buttonId);
                    }
                    break;
                }
                case NEW: {
                    if ("Save".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedPersist(getEntity());
                    }
                    if ("Cancel".equals(buttonId)) {
                        return true;
                    }
                    break;
                }
                case UPDATE: {
                    if ("Save".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedUpdate(getEntity());
                    }
                    if ("Cancel".equals(buttonId)) {
                        return true;
                    }
                    if ("Remove".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedRemove(getEntity());
                    }
                    if ("Archive".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager()
                                .isAllowedUpdate(getEntity())
                                && objService.isArchivable(getModel().getEntityName());
                    }
                    if (objService.isEntityAction(getEntity().getName(), buttonId, getModel().getCurrentObj())) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedKey(getEntity(), buttonId);
                    }
                    break;
                }
            }
            return false;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public PModel getModel() {
        return (PModel) super.getModel();
    }

    public String getEntityName() {
        return getModel().getEntityName();
    }

    public Entity getEntity() {
        return UPA.getPersistenceUnit().getEntity(getModel().getEntityName());
    }

    @Override
    protected void updateMode(EditCtrlMode m) {
        enabledButtons.clear();
        super.updateMode(m);
        updateModelFromConfig();
        updateView();
    }

    public void setEntityName(String entityName) {
        enabledButtons.clear();
        try {
            UPA.getPersistenceUnit().getEntity(entityName);
            getModel().setEntityName(entityName);
            getModel().setList(new ArrayList<ObjRow>());
            getModel().setCurrent(delegated_newInstance());
            updateMode(EditCtrlMode.LIST);
            currentModelToView();
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public DynaFormModel getDynModel() {
        return dynModel;
    }

    public void setDynModel(DynaFormModel dynModel) {
        this.dynModel = dynModel;
    }

    public void onSaveCurrent() {
        enabledButtons.clear();
        try {
            currentViewToModel();
            switch (getModel().getMode()) {
                case NEW: {
                    Object c = getModel().getCurrentObj();
                    objService.save(c);
                    onCancelCurrent();
                    break;
                }
                case UPDATE: {
                    Object c = getModel().getCurrentObj();
                    objService.save(c);
                    onCancelCurrent();
                    break;
                }
            }
            reloadPage();
            updateMode(EditCtrlMode.LIST);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onDeleteCurrent() {
        enabledButtons.clear();
        int count = 0;
        try {
            if (getModel().getMode() == EditCtrlMode.LIST) {
                for (ObjRow row : getModel().getList()) {
                    if (row.isSelected()) {
                        objService.remove(getEntityName(), objService.resolveId(row.getValue()));
                        count++;
                    }
                }
                reloadPage();
            } else {
                currentViewToModel();
                Object c = getModel().getCurrentObj();
                objService.remove(getEntityName(), objService.resolveId(c));
                count++;
                reloadPage();
                updateMode(EditCtrlMode.LIST);
            }
            if (count == 0) {
                FacesUtils.addInfoMessage(null, "Aucun Enregistrement supprimé");
            } else {
                FacesUtils.addInfoMessage(null, count + " Enregistrement(s) supprimé(s)");
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(null, count + " Erreur : " + ex);
        }
    }

    public void onArchiveCurrent() {
        enabledButtons.clear();
        int count = 0;
        try {
            if (getModel().getMode() == EditCtrlMode.LIST) {
                for (ObjRow row : getModel().getList()) {
                    if (row.isSelected()) {
                        objService.archive(getEntityName(), objService.resolveId(row.getValue()));
                        count++;
                    }
                }
                reloadPage();
            } else {
                currentViewToModel();
                Object c = getModel().getCurrentObj();
                objService.archive(getEntityName(), objService.resolveId(c));
                getModel().setCurrent(delegated_newInstance());
                updateMode(EditCtrlMode.LIST);
            }
            if (count == 0) {
                FacesUtils.addInfoMessage(null, "Aucun Enregistrement archivé");
            } else {
                FacesUtils.addInfoMessage(null, count + " Enregistrement(s) archivé(s)");
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(null, count + " Erreur : " + ex);
        }
    }

    @OnPageLoad
    @Override
    public void reloadPage(String cmd) {
        enabledButtons.clear();
        try {
            Config cfg = VrHelper.parseJSONObject(cmd, Config.class);
            getModel().setConfig(cfg);
            getModel().setDisabledFields(new HashSet<String>());
            if (cfg.disabledFields != null) {
                for (String disabledField : cfg.disabledFields) {
                    if (!StringUtils.isEmpty(disabledField)) {
                        getModel().getDisabledFields().add(disabledField);
                    }
                }
            }
            getModel().setCmd(cmd);
            setEntityName(cfg.entity);
            List<Object> found = objService.findByFilter(getEntityName(), cfg.listFilter);
            List<ObjRow> filteredObjects = new ArrayList<>();
            Entity entity = getEntity();
            UPASecurityManager sm = entity.getPersistenceUnit().getSecurityManager();
            for (Object o : found) {
                Object id = entity.getBuilder().entityToId(o);
                if (sm.isAllowedNavigate(entity, id, o)) {
                    ObjRow r = new ObjRow(o);
                    r.setRead(sm.isAllowedLoad(entity, id, o));
                    r.setWrite(sm.isAllowedUpdate(entity, id, o));
                    filteredObjects.add(r);
                }
            }
            getModel().setList(filteredObjects);
            if (cfg.id == null || cfg.id.trim().length() == 0) {
                updateMode(EditCtrlMode.LIST);
                getModel().setCurrent(null);
            } else {
                Object eid = resolveEntityId(cfg.id);
                Object curr = objService.find(getEntityName(), eid);
                ObjRow r = new ObjRow(curr);
                r.setRead(sm.isAllowedLoad(entity, eid, curr));
                r.setWrite(sm.isAllowedUpdate(entity, eid, curr));
                getModel().setCurrent(r);
                updateMode(EditCtrlMode.UPDATE);
                currentModelToView();
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    private Object resolveEntityId(String strId) {
        if (strId == null || strId.trim().length() == 0) {
            return null;
        }
        return Convert.toInteger(strId);
    }

    public Object getSortLayoutPropertyValue(Object o, String property) {
        return getLayoutPropertyValue(o, property);
    }

    public String getRowStyle() {
        Entity p = getEntity();
        String v = p.getProperties().getString(UIConstants.Grid.ROW_STYLE);
        if (StringUtils.isEmpty(v)) {
            return v;
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        String val = null;

        try {
            ELContext elContext = fc.getELContext();
            String expr = "${" + v + "}";
            ValueExpression targetExpression
                    = fc.getApplication().getExpressionFactory().createValueExpression(elContext, expr, String.class);
            val = (String) targetExpression.getValue(elContext);
//            val = (String) elContext.getELResolver().getValue(elContext, null, v);
        } catch (RuntimeException e) {
            throw new FacesException(e.getMessage(), e);
        }

        return val;
    }

    public Object getPropertyColumnStyle(String property) {
        Field p = getEntity().getField(property);
        String v = p.getProperties().getString(UIConstants.Grid.COLUMN_STYLE);
        return v;
    }

    public Object getFilterLayoutPropertyValue(Object o, String property) {
        return getLayoutPropertyValue(o, property);
    }

    public Object getLayoutPropertyValue(Object o, String property) {
        try {
            if (o == null) {
                return null;
            }
            Entity e = UPA.getPersistenceUnit().getEntity(o.getClass());
            Object v = e.getBuilder().getProperty(o, property);
            if (v != null) {
                DataType d = (DataType) e.getField(property).getDataType();
                if (d instanceof EntityType) {
                    EntityType ed = (EntityType) d;
                    v = ed.getRelationship().getTargetEntity().getBuilder().getMainValue(v);
                } else if (d instanceof EnumType) {
                    v = i18n.getEnum(v);
                }
            }
            return v;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public String getObjectName(Object obj) {
        return objService.getObjectName(obj);
    }

    public class PModel extends Model<ObjRow> {

        private String entityName;
        private List<ObjFormAction> actions = new ArrayList<ObjFormAction>();
        private Config config;
        private Set<String> disabledFields = new HashSet<String>();

        public PModel() {
            setCurrent(null);
        }

        public List<ObjFormAction> getActions() {
            return actions;
        }

        public void setActions(List<ObjFormAction> actions) {
            this.actions = actions;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public Object getCurrentObj() {
            ObjRow c = getCurrent();
            if (c == null) {
                return null;
            }
            return c.getValue();
        }

        public ObjRow getCurrent() {
            return super.getCurrent();
        }

        public List<ObjRow> getList() {
            return super.getList();
        }

        @Override
        public void setCurrent(ObjRow current) {
            super.setCurrent(current); //To change body of generated methods, choose Tools | Templates.
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public Set<String> getDisabledFields() {
            return disabledFields;
        }

        public void setDisabledFields(Set<String> disabledFields) {
            this.disabledFields = disabledFields;
        }

    }

    @Override
    protected ObjRow delegated_newInstance() {
        enabledButtons.clear();
        try {
            if (getModel().getEntityName() != null) {
                Entity e = UPA.getPersistenceUnit().getEntity(getModel().getEntityName());
                Object o = e.createEntity();
                for (Field field : e.getFields()) {
                    Object v = field.getDefaultValue();
                    if (v != null) {
                        e.getBuilder().setProperty(o, field.getName(), v);
                    }
                }
                Object i = o;
                ObjRow r = new ObjRow(i);
                r.setRead(true);
                r.setWrite(true);
                r.setSelected(false);

                return r;
            }
            return new ObjRow(new Object());
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public List<ColumnView> getColumns() {
        return columns;
    }

    public void updateModelFromConfig() {
        enabledButtons.clear();
        Config cfg = VrHelper.parseJSONObject(getModel().getCmd(), Config.class);
        Object o = getModel().getCurrentObj();
        Entity e = getEntity();
        EntityBuilder builder = e.getBuilder();
        if (cfg != null && cfg.values != null && !cfg.values.isEmpty() && o != null && e != null) {
            for (Map.Entry<String, String> entrySet : cfg.values.entrySet()) {
                String fieldName = entrySet.getKey();
                String fieldValueString = entrySet.getValue();
                Field field = e.findField(fieldName);
                if (field != null) {
                    DataType t = field.getDataType();
                    if (t instanceof StringType) {
                        builder.setProperty(o, fieldName, fieldValueString);
                    } else if (t instanceof IntType) {
                        builder.setProperty(o, fieldName, Integer.parseInt(fieldValueString));
                    } else if (t instanceof TemporalType) {
                        Date v = null;
                        boolean set = false;
                        if (StringUtils.isEmpty(fieldValueString)) {
                            set = true;
                        } else {
                            for (String df : new String[]{}) {
                                SimpleDateFormat d = new SimpleDateFormat(df);
                                try {
                                    v = d.parse(fieldValueString);
                                    set = true;
                                    break;
                                } catch (ParseException ex) {
                                    //ignore
                                }
                            }
                        }
                        if (set) {
                            builder.setProperty(o, fieldName, v);
                        }
                    } else if (t instanceof EntityType) {
                        EntityType et = (EntityType) t;
                        Entity re = UPA.getPersistenceUnit().getEntity(et.getReferencedEntityName());
                        List<Field> rpp = re.getPrimaryFields();
                        if (rpp.size() == 1 && PlatformTypes.isInteger(fieldValueString)) {
                            Object v = re.findById(Integer.parseInt(fieldValueString));
                            builder.setProperty(o, fieldName, v);
                        } else {
                            System.err.println("Not supported yet");
                        }
                    } else {
                        System.err.println("Not supported yet");
                    }
                }
            }
        }
    }

    public void currentModelToView() {
        List<ObjFormAction> act = new ArrayList<>();
        for (ActionInfo a : objService.getEntityActionList(getModel().getEntityName(), getModel().getCurrentObj())) {
            act.add(new ObjFormAction(a.getLabel(), a.getStyle(), a.getId()));
        }
        getModel().setActions(act);
        try {
            for (PropertyView property : properties) {
                property.loadFrom(getModel().getCurrentObj());
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public Map<String, Object> currentViewToMap() {
        Map<String, Object> map = new HashMap<>();
        try {
            for (PropertyView property : properties) {
                property.storeToMap(map);
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
        return map;
    }

    public void currentViewToModel() {
        try {
            for (PropertyView property : properties) {
                property.storeTo(getModel().getCurrentObj());
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void updateView() {
        enabledButtons.clear();
        try {
            //reset table state
            try {
                UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":listForm:listTable");
                table.setValueExpression("sortBy", null);
            } catch (Exception ex) {
                //ignore
            }
            try {
                VrApp.getBean(VrMenuManager.class).getModel().getTitleCrumb().setTitle(getPageTitleString(getEntity(), getModel().getMode()));
                UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":listForm:listTable");
                table.setValueExpression("sortBy", null);
            } catch (Exception ex) {
                //ignore
            }
            //update columns
            Entity ot = UPA.getPersistenceUnit().getEntity(getEntityName());
            columns.clear();
            properties.clear();
            boolean adm = VrApp.getBean(CorePlugin.class).isActualAdmin();
            for (Field field : ot.getFields(Fields.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY))) {
                AccessLevel ral = field.getReadAccessLevel();
                if (ral == AccessLevel.PRIVATE) {
                    continue;
                } else if (ral == AccessLevel.PROTECTED) {
                    if (!adm) {
                        continue;
                    }
                }
                String type = UIConstants.ControlType.TEXT;
                if (PlatformTypes.isBooleanType(field.getDataType().getPlatformType())) {
                    type = UIConstants.ControlType.CHECKBOX;
                }
                String property = field.getName();
                String propertyExpr = field.getName();
                if (field.getDataType() instanceof EntityType) {
                    EntityType e = (EntityType) field.getDataType();
                    propertyExpr += "." + e.getRelationship().getTargetEntity().getMainField().getName();
                }
                columns.add(new ColumnView(i18n.get(field), property, propertyExpr, type));
            }
            FormHelper h = new FormHelper();
            dynModel = h.dynModel;

            List<OrderedPropertyView> propertyViews = new ArrayList<OrderedPropertyView>();
            for (Field field : ot.getFields(Fields.byModifiersNoneOf(FieldModifier.SYSTEM))) {
                Map<String, Object> config = new HashMap<>();
                if (getModel().getDisabledFields().contains(field.getName())) {
                    config.put("disabled", true);
                }
                PropertyView[] ctrls = propertyViewManager.createPropertyView(field.getName(), field, config);
                if (ctrls != null) {
                    for (PropertyView ctrl : ctrls) {
                        if (ctrl != null) {
                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), ctrl));
                        }
                    }
                }
            }
            if (getModel().getMode() == EditCtrlMode.UPDATE) {
                boolean firstDetailRelation = true;
                int counter = 0;
                int maxPerLine = 3;
                for (Relationship relation : ot.getRelationships()) {
                    if (relation.getTargetEntity().getName().equals(ot.getName())) {
                        if (relation.getRelationshipType() == RelationshipType.COMPOSITION) {
                            EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.ControlType.ENTITY_DETAIL, propertyViewManager);
                            details.setPrependNewLine((counter % maxPerLine) == 0);
                            details.setAppendNewLine(false);
                            details.setColspan(1);
                            counter++;
                            if (firstDetailRelation) {
                                details.setSeparatorText("Details");
                                firstDetailRelation = false;
                            }
                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                        }
                    }
                }
                counter = 0;
                for (Relationship relation : ot.getRelationships()) {
                    if (relation.getTargetEntity().getName().equals(ot.getName())) {
                        if (relation.getRelationshipType() == RelationshipType.AGGREGATION) {
                            EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.ControlType.ENTITY_DETAIL, propertyViewManager);
                            details.setPrependNewLine((counter % maxPerLine) == 0);
                            details.setAppendNewLine(false);
                            details.setColspan(1);
                            counter++;
                            if (firstDetailRelation) {
                                details.setSeparatorText("Details");
                                firstDetailRelation = false;
                            }
                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                        }
                    }
                }
                counter = 0;
                boolean firstAssoRelation = true;
                for (Relationship relation : ot.getRelationships()) {
                    if (relation.getTargetEntity().getName().equals(ot.getName())) {
                        final RelationshipType t = relation.getRelationshipType();
                        if (t != RelationshipType.AGGREGATION && t != RelationshipType.COMPOSITION) {
                            EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.ControlType.ENTITY_DETAIL, propertyViewManager);
                            details.setPrependNewLine((counter % maxPerLine) == 0);
                            details.setAppendNewLine(false);
                            details.setColspan(1);
                            counter++;
                            if (firstAssoRelation) {
                                details.setSeparatorText("References");
                                firstAssoRelation = false;
                            }
                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                        }
                    }
                }
            }

            Collections.sort(propertyViews);
            for (OrderedPropertyView ctrlv : propertyViews) {
                PropertyView ctrl = ctrlv.getValue();
                properties.add(ctrl);
                boolean hasSeparator = ctrl.getSeparatorText() != null && ctrl.getSeparatorText().length() > 0;
                if (hasSeparator || ctrl.isPrependNewLine()) {
                    h.newLine();
                }
                DynaFormLabel label = null;
                for (int i = 0; i < ctrl.getPrependEmptyCells(); i++) {
                    h.addLabel("", 1, 1);
                }
                if (hasSeparator) {
                    h.addControl(ctrl.getSeparatorText(), UIConstants.ControlType.SEPARATOR, Integer.MAX_VALUE, 1);
                }
                if (!ctrl.isNoLabel()) {
                    label = h.addLabel(ctrl.getName(), 1, 1);
                    if (ctrl.isLabelNewLine()) {
                        h.newLine();
                    }
                    for (int i = 0; i < ctrl.getAppendEmptyCells(); i++) {
                        h.addLabel("", 1, 1);
                    }
                }

                DynaFormControl control12 = h.addControl(ctrl, ctrl.getCtrlType(), ctrl.getColspan(), ctrl.getRowpan());
                if (label != null) {
                    label.setForControl(control12);
                }
                if (ctrl.isAppendNewLine()) {
                    h.newLine();
                }

            }
            currentModelToView();
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    private static class FormHelper {

        DynaFormModel dynModel = new DynaFormModel();
        DynaFormRow row = null;
        int cols = 0;
        int maxcols = 4;

        private void revalidateRow() {
            if (row == null) {
                row = dynModel.createRegularRow();
            }
        }

        private void newLine() {
            cols = 0;
            row = null;
        }

        public DynaFormControl addControl(Object data, String type, int colspan, int rowspan) {
            revalidateRow();
            colspan = validColspan(colspan);
            rowspan = validRowspan(rowspan);
            DynaFormControl cc = row.addControl(data, type, colspan, rowspan);
            nextPosition(colspan, rowspan);
            return cc;
        }

        public DynaFormLabel addLabel(String data, int colspan, int rowspan) {
            revalidateRow();
            colspan = validColspan(colspan);
            rowspan = validRowspan(rowspan);
            DynaFormLabel lab = row.addLabel(data, colspan, rowspan);
            nextPosition(colspan, rowspan);
            return lab;
        }

        private int validColspan(int colspan) {
            return (colspan <= 0 || colspan == Integer.MAX_VALUE) ? maxcols - cols : colspan;
        }

        private int validRowspan(int rowspan) {
            return (rowspan <= 0 || rowspan == Integer.MAX_VALUE) ? 1 : rowspan;
        }

        private void nextPosition(int colspan, int rowspan) {
            cols += colspan;
            if (cols >= maxcols) {
                newLine();
            }
        }
    }

    @Override
    public void onSelectCurrent() {
        enabledButtons.clear();
        try {
            super.onSelectCurrent();
            currentModelToView();
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
        VrMenuManager menu = VrApp.getBean(VrMenuManager.class);
        if (getModel().getCurrent() != null) {
            Config cfg = new Config();
            cfg.entity = getEntityName();
            cfg.id = String.valueOf(getEntity().getBuilder().entityToId(getModel().getCurrent().getValue()));
            menu.pushHistory("obj", cfg);
        }
    }

    public void onCancelCurrent() {
        enabledButtons.clear();
        super.onCancelCurrent();
        VrMenuManager menu = VrApp.getBean(VrMenuManager.class);
        if (getModel().getCurrent() != null) {
            Config cfg = new Config();
            cfg.entity = getEntityName();
            cfg.id = String.valueOf(getEntity().getBuilder().entityToId(getModel().getCurrent().getValue()));
            cfg.values = getModel().getConfig().values;
            cfg.listFilter = getModel().getConfig().listFilter;
            cfg.disabledFields = getModel().getConfig().disabledFields;
            menu.pushHistory("obj", cfg);
        }
    }

    public List<PropertyView> getProperties() {
        return properties;
    }

    public static final class Config {

        public String entity;
        public String id;
        public String listFilter;
        public Map<String, String> values;
        public String[] disabledFields;
    }

    public boolean filterByProperty(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.findEntity(value.getClass());
        if (e != null) {
            value = e.getBuilder().getMainValue(value);
        }
        String svalue = value == null ? "" : String.valueOf(value);
        String sfilter = String.valueOf(filterText);
        return svalue.toLowerCase().contains(sfilter.toLowerCase());
    }

    public void onAction(String actionKey, Object[] args) {
        enabledButtons.clear();
        if ("Persist".equals(actionKey)) {
            onNew();
        } else if ("Save".equals(actionKey)) {
            onSaveCurrent();
        } else if ("Remove".equals(actionKey)) {
            onDeleteCurrent();
        } else {
            try {
                currentViewToModel();
                Object c = getModel().getCurrentObj();
                objService.invokeEntityAction(getEntityName(), actionKey, c, args);
            } catch (RuntimeException ex) {
                log.log(Level.SEVERE, "Error", ex);
                throw ex;
            }
        }
    }

    public void updateAllFormulas() {
        enabledButtons.clear();
        UPA.getPersistenceUnit().updateFormulas();
    }

    public PropertyView findPropertyView(String componentId) {
        for (PropertyView p : properties) {
            if (p.getComponentId().equals(componentId)) {
                return p;
            }
        }
        return null;
    }

    public void openActionDialog(String actionId) {
        if (actionId != null) {
            ActionDialog ed = VrApp.getBean(ActionDialogManager.class).getActionDialog(actionId);
            if (ed != null) {
                ed.openDialog(actionId, actionId);
                return;
            }
        }
        onAction(actionId, null);
    }

    public void onActionDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        if (o != null) {
            onAction(o.getUserInfo(), (Object[]) o.getValue());
        }
    }

    public void openPropertyViewDialog(String property) {
        PropertyView p = findPropertyView(property);
        if (p != null) {
            PropertyViewDialog ed = VrApp.getBean(PropertyViewDialogManager.class).getPropertyViewDialog(p.getCtrlType());
            if (ed != null) {
                ed.openDialog(p, p.getComponentId());
            }
        }
    }

    public void onPropertyViewDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        PropertyView p = findPropertyView(o.getUserInfo());
        if (p != null) {
            p.setValue(o.getValue());
        }
    }
}
