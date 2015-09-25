/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.web.obj;

import net.vpc.app.vainruling.api.ui.UIConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
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
import net.vpc.common.utils.Convert;
import net.vpc.common.utils.PlatformTypes;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.FieldModifier;
import net.vpc.upa.Package;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.Relationship;
import net.vpc.upa.RelationshipType;
import net.vpc.upa.UPA;
import net.vpc.upa.filters.Fields;
import net.vpc.upa.impl.util.Strings;
import net.vpc.upa.types.EntityType;
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
public class ObjCtrl extends AbstractObjectCtrl<Object> implements UCtrlProvider {

    @Autowired
    private ObjManagerService objService;
    private Config config;
    @Autowired
    private PropertyViewManager propertyViewManager;
    @Autowired
    private I18n i18n;
    private final List<ColumnView> columns = new ArrayList<>();
    private final List<PropertyView> properties = new ArrayList<>();
    private DynaFormModel dynModel = new DynaFormModel();
    private static final Logger log = Logger.getLogger(ObjCtrl.class.getName());

    public ObjCtrl() {
        super(null);
        this.model = new PModel();
    }

    @Override
    public UCtrlData getUCtrl(String cmd) {
        try {
            Config c = VrHelper.parseObject(cmd, Config.class);
            Entity entity = UPA.getPersistenceUnit().getEntity(c.entity);
            UCtrlData d = new UCtrlData();
            d.setTitle(getPageTitleString(entity, EditCtrlMode.LIST));
            d.setUrl("modules/obj/objects");
            d.setCss("fa-table");
            List<BreadcrumbItem> items = new ArrayList<>();
            
            Package p = entity.getParent();
            int pos=items.size();
            while(p!=null && !"/".equals(p.getPath())){
                items.add(pos,new BreadcrumbItem(i18n.get(p), "fa-dashboard", "", ""));
                p=p.getParent();
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
        try {
            switch (getModel().getMode()) {
                case LIST: {
                    if ("Refresh".equals(buttonId)) {
                        return true;
                    }
                    if ("New".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedPersist(getEntity());
                    }
                    if ("Cancel".equals(buttonId)) {
                        return false;
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
                    if (objService.isEntityAction(getEntity().getName(), buttonId, null)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedKey(getEntity(), buttonId);
                    }
                    break;
                }
                case UPDATE: {
                    if ("Save".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedUpdate(getEntity());
                    }
                    if ("Remove".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager().isAllowedRemove(getEntity());
                    }
                    if ("Cancel".equals(buttonId)) {
                        return true;
                    }
                    if ("Archive".equals(buttonId)) {
                        return objService.isArchivable(getModel().getEntityName());
                    }
                    if (objService.isEntityAction(getEntity().getName(), buttonId, getModel().getCurrent())) {
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
        super.updateMode(m);
        updateView();
    }

    public void setEntityName(String entityName) {
        try {
            UPA.getPersistenceUnit().getEntity(entityName);
            getModel().setEntityName(entityName);
            getModel().setList(new ArrayList<>());
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
        try {
            currentViewToModel();
            switch (getModel().getMode()) {
                case NEW: {
                    Object c = getModel().getCurrent();
                    objService.save(c);
                    onCancelCurrent();
                    break;
                }
                case UPDATE: {
                    Object c = getModel().getCurrent();
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
        try {
            currentViewToModel();
            Object c = getModel().getCurrent();
            objService.remove(getEntityName(), objService.resolveId(c));
            updateMode(EditCtrlMode.LIST);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void onArchiveCurrent() {
        try {
            currentViewToModel();
            Object c = getModel().getCurrent();
            objService.archive(getEntityName(), objService.resolveId(c));
            getModel().setCurrent(delegated_newInstance());
            updateMode(EditCtrlMode.LIST);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    @OnPageLoad
    @Override
    public void reloadPage(String cmd) {
        try {
            Config cfg = VrHelper.parseObject(cmd, Config.class);
            this.config = cfg;
            getModel().setCmd(cmd);
            setEntityName(cfg.entity);
            getModel().setList(objService.findByFilter(getEntityName(), cfg.listFilter));
            if (cfg.id == null) {
                updateMode(EditCtrlMode.LIST);
                getModel().setCurrent(null);
            } else {
                Object curr = objService.find(getEntityName(), resolveEntityId(cfg.id));
                getModel().setCurrent(curr);
                updateMode(EditCtrlMode.UPDATE);
                currentModelToView();
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    private Object resolveEntityId(String strId) {
        if (strId == null || strId.length() == 0) {
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
        if (Strings.isNullOrEmpty(v)) {
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
                Entity e2 = UPA.getPersistenceUnit().findEntity(v.getClass());
                if (e2 != null) {
                    v = e2.getBuilder().getMainValue(v);
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

    public class PModel extends Model<Object> {

        private String entityName;
        private Object[] actionArgs;
        private List<ObjFormAction> actions = new ArrayList<ObjFormAction>();

        public PModel() {
            setCurrent(null);
        }

        public List<ObjFormAction> getActions() {
            return actions;
        }

        public void setActions(List<ObjFormAction> actions) {
            this.actions = actions;
        }

        public Object[] getActionArgs() {
            return actionArgs;
        }

        public void setActionArgs(Object[] actionArgs) {
            this.actionArgs = actionArgs;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public Object getCurrent() {
            return super.getCurrent();
        }

        public List<Object> getList() {
            return super.getList();
        }

        @Override
        public void setCurrent(Object current) {
            super.setCurrent(current); //To change body of generated methods, choose Tools | Templates.
        }

    }

    @Override
    protected Object delegated_newInstance() {
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
                return o;
            }
            return new Object();
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public List<ColumnView> getColumns() {
        return columns;
    }

    public void currentModelToView() {
        List<ObjFormAction> act = new ArrayList<>();
        for (ActionInfo a : objService.getEntityActionList(getModel().getEntityName(), getModel().getCurrent())) {
            act.add(new ObjFormAction(a.getLabel(), a.getStyle(), a.getId()));
        }
        getModel().setActions(act);
        try {
            for (PropertyView property : properties) {
                property.loadFrom(getModel().getCurrent());
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
                property.storeTo(getModel().getCurrent());
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public void updateView() {
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

            for (Field field : ot.getFields(Fields.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY))) {
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
            FomrHelper h = new FomrHelper();
            dynModel = h.dynModel;

            List<OrderedPropertyView> propertyViews = new ArrayList<OrderedPropertyView>();
            for (Field field : ot.getFields(Fields.byModifiersNoneOf(FieldModifier.SYSTEM))) {
                PropertyView[] ctrls = propertyViewManager.createPropertyView(field.getName(), field);
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
                for (Relationship relation : ot.getRelationships()) {
                    if (relation.getTargetEntity().getName().equals(ot.getName())) {
                        if (relation.getRelationshipType() == RelationshipType.COMPOSITION) {
                            EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.ControlType.ENTITY_DETAIL, propertyViewManager);
                            if (firstDetailRelation) {
                                details.setSeparatorText("Details");
                                firstDetailRelation = false;
                            }
                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                        }
                    }
                }
                for (Relationship relation : ot.getRelationships()) {
                    if (relation.getTargetEntity().getName().equals(ot.getName())) {
                        if (relation.getRelationshipType() == RelationshipType.AGGREGATION) {
                            EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.ControlType.ENTITY_DETAIL, propertyViewManager);
                            if (firstDetailRelation) {
                                details.setSeparatorText("Details");
                                firstDetailRelation = false;
                            }
                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                        }
                    }
                }
                boolean firstAssoRelation = true;
                for (Relationship relation : ot.getRelationships()) {
                    if (relation.getTargetEntity().getName().equals(ot.getName())) {
                        final RelationshipType t = relation.getRelationshipType();
                        if (t != RelationshipType.AGGREGATION && t != RelationshipType.COMPOSITION) {
                            EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.ControlType.ENTITY_DETAIL, propertyViewManager);
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

    private static class FomrHelper {

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
        try {
            super.onSelectCurrent();
            currentModelToView();
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public List<PropertyView> getProperties() {
        return properties;
    }

    public static final class Config {

        public String entity;
        public String id;
        public String listFilter;
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

    public void onAction(String actionKey) {
        if ("Persist".equals(actionKey)) {
            onNew();
        } else if ("Save".equals(actionKey)) {
            onSaveCurrent();
        } else if ("Remove".equals(actionKey)) {
            onDeleteCurrent();
        } else {
            try {
                currentViewToModel();
                Object c = getModel().getCurrent();
                objService.invokeEntityAction(getEntityName(), actionKey, c, getModel().getActionArgs());
            } catch (RuntimeException ex) {
                log.log(Level.SEVERE, "Error", ex);
                throw ex;
            }
        }
    }

    public void updateAllFormulas() {
        UPA.getPersistenceUnit().updateFormulas();
    }
}
