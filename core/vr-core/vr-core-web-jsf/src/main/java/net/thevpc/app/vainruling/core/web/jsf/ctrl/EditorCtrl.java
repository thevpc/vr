/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogAdapter;
import net.thevpc.app.vainruling.core.service.editor.DefaultEditorFieldSelection;
import net.thevpc.app.vainruling.core.service.editor.EditorActionConfig;
import net.thevpc.app.vainruling.core.service.editor.EditorFieldSelection;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.app.vainruling.core.web.VrColorTable;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.obj.*;
import net.thevpc.app.vainruling.VrPageHistoryItem;
import net.thevpc.app.vainruling.VrPageInfo;
import net.thevpc.app.vainruling.core.service.editor.AutoFilterData;
import net.thevpc.app.vainruling.core.service.editor.AutoFilter;
import net.thevpc.app.vainruling.core.service.editor.EditorRow;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResult;
import net.thevpc.app.vainruling.core.service.editor.EditorConfig;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogResultPostProcess;
import net.thevpc.app.vainruling.core.service.editor.ActionDialogManager;
import net.thevpc.app.vainruling.core.service.editor.ColumnView;
import net.thevpc.app.vainruling.core.service.editor.ViewContext;
import net.thevpc.app.vainruling.core.service.editor.DialogResult;
import net.thevpc.app.vainruling.core.service.editor.ParamType;
import net.thevpc.app.vainruling.core.service.editor.ActionParam;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.*;
import net.thevpc.app.vainruling.core.web.*;
import net.thevpc.app.vainruling.core.web.jsf.DialogBuilder;
import net.thevpc.app.vainruling.core.web.jsf.ctrl.obj.*;
import net.thevpc.app.vainruling.VrBreadcrumbItem;
import net.thevpc.app.vainruling.core.web.menu.VrMenuManager;
import net.thevpc.common.jsf.FacesUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.*;
import net.thevpc.common.util.PlatformUtils;
import net.thevpc.upa.*;
import net.thevpc.upa.Package;
import net.thevpc.upa.expressions.Expression;
import net.thevpc.upa.expressions.Literal;
import net.thevpc.upa.filters.FieldFilters;
import net.thevpc.upa.types.*;
import org.primefaces.event.SelectEvent;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import net.thevpc.app.vainruling.VrAccessMode;
import net.thevpc.app.vainruling.core.web.jsf.Vr;
import net.thevpc.app.vainruling.VrPageInfoResolver;
import net.thevpc.app.vainruling.VrPage;
import net.thevpc.app.vainruling.VrPathItem;
import net.thevpc.app.vainruling.VrOnPageLoad;
import net.thevpc.app.vainruling.VrEditorSearch;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage(
        breadcrumb = {
            @VrPathItem(title = "Entité", css = "fa-dashboard", ctrl = "")},
        //        css = "fa-table",
        //        title = "Liste Entités",
        url = "modules/editor/editor"
)
@Controller
@ManagedBean
public class EditorCtrl extends AbstractObjectCtrl<EditorRow> implements VrPageInfoResolver {

    public static final String[] CSS_COLOR_ARR = {"vr-label-bg01", "vr-label-bg02", "vr-label-bg03", "vr-label-bg04", "vr-label-bg05", "vr-label-bg06", "vr-label-bg07", "vr-label-bg08", "vr-label-bg09", "vr-label-bg10", "vr-label-bg11", "vr-label-bg12", "vr-label-bg13", "vr-label-bg14", "vr-label-bg15", "vr-label-bg16", "vr-label-bg17", "vr-label-bg18", "vr-label-bg19", "vr-label-bg20"};

    private static final Logger log = Logger.getLogger(EditorCtrl.class.getName());
    private final List<ColumnView> columns = new ArrayList<>();
    private final List<PropertyView> properties = new ArrayList<>();
    private final List<AutoFilter> autoFilters = new ArrayList<>();
    private final Map<String, Boolean> enabledButtons = new HashMap<>();
    @Autowired
    private CorePlugin core;
    @Autowired
    private ActionDialogManager actionDialogManager;
    @Autowired
    private PropertyViewManager propertyViewManager;
    @Autowired
    private I18n i18n;
    private DynaFormModel dynModel = new DynaFormModel();

    public EditorCtrl() {
        super(null);
        this.model = new PModel();
    }

    @Override
    public VrPageInfo resolvePageInfo(String cmd) {
        try {
            EditorConfig c = VrUtils.parseJSONObject(cmd, EditorConfig.class);
            Entity entity = UPA.getPersistenceUnit().getEntity(c.entity);
            VrPageInfo d = new VrPageInfo();
            d.setControllerName("editor");
            d.setCmd(cmd);
            d.setTitle(getPageTitleString(entity, VrAccessMode.READ));
            d.setUrl("modules/editor/editor");
            d.setCss("fa-table");
            d.setSecurityKey(CorePluginSecurity.getEntityRightEditor(entity));
            List<VrBreadcrumbItem> items = new ArrayList<>();

            Package p = entity.getParent();
            int pos = items.size();
            while (p != null && !"/".equals(p.getPath())) {
                Package pp = p.getParent();
                items.add(pos, new VrBreadcrumbItem(p.getTitle(), pp == null ? null : i18n.get(pp), "fa-dashboard", "", ""));
                p = p.getParent();
            }
            d.setBreadcrumb(items.toArray(new VrBreadcrumbItem[items.size()]));
            return d;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error", ex);
        }
        return null;
    }

    public String getPageTitleString(Entity entity, VrAccessMode mode) {
        if (mode == null) {
            mode = VrAccessMode.READ;
        }
        if (entity == null) {
            switch (mode) {
                case READ: {
                    return ("Liste Objets");
                }
                case UPDATE: {
                    return ("Mise à jour Objet");
                }
                case BULK_UPDATE: {
                    return ("Mise à jour par Lot");
                }
                case PERSIST: {
                    return ("Nouvel Objet");
                }
            }
            return ("???");
        }
        String newTitleString = "???";
        Map<String, Object> args = new HashMap<>();
        String entityPlainTitle = entity.getTitle();
        args.put("title", entityPlainTitle);
        args.put("name", entity.getName());
        switch (mode) {
            case READ: {
                newTitleString = i18n.getOrNull(entity.getI18NTitle().append("ListTitle"), args);
                if (newTitleString == null) {
                    newTitleString = "Liste " + entityPlainTitle;
                }
                break;
            }
            case UPDATE: {
                newTitleString = i18n.getOrNull(entity.getI18NTitle().append("UpdateTitle"), args);
                if (newTitleString == null) {
                    newTitleString = "Mise à jour " + entityPlainTitle;
                }
                break;
            }
            case BULK_UPDATE: {
                newTitleString = i18n.getOrNull(entity.getI18NTitle().append("BulkUpdateTitle"), args);
                if (newTitleString == null) {
                    newTitleString = "Mise à jour par Lot " + entityPlainTitle;
                }
                break;
            }
            case PERSIST: {
                newTitleString = i18n.getOrNull(getEntity().getI18NTitle().append("NewTitle"), args);
                if (newTitleString == null) {
                    newTitleString = "Nouveau " + entityPlainTitle;
                }
                break;
            }
        }
        return newTitleString;
    }

    public boolean isCustomCheckboxSelection() {
        return false;
    }

    @Override
    public boolean isEnabledButton(String buttonId) {
        synchronized (enabledButtons) {
            Boolean v = enabledButtons.computeIfAbsent(buttonId, this::isEnabledButtonImpl);
            return v.booleanValue();
        }
    }

    protected boolean isSaveAllowed() {
        Entity e = getEntity();
        if (e == null) {
            return false;
        }
        switch (getModel().getMode()) {

            case PERSIST: {
                return e.getShield().isPersistSupported() && e.getShield().isPersistEnabled();
            }
            case UPDATE: {
                return e.getShield().isUpdateSupported() && e.getShield().isUpdateEnabled();
            }
            case BULK_UPDATE: {
                return isEnabledBulkUpdate();
            }

        }
        return false;
    }

    public boolean isEnabledBulkUpdate() {
        UPASecurityManager sm = UPA.getPersistenceUnit().getSecurityManager();
        Entity e = getEntity();
        if (e == null) {
            return false;
        }
        boolean allowedUpdate = e.getShield().isUpdateSupported() && e.getShield().isUpdateEnabled();
        if (!allowedUpdate) {
            return false;
        }
        if (!CorePlugin.get().isCurrentSessionAdmin()) {
            return false;
        }
        boolean someSelection = true;
//        for (EditorRow editorRow : getModel().getList()) {
//            if (editorRow.isSelected()) {
//                someSelection = true;
//                if (!sm.isAllowedUpdate(getEntity(), getCurrentId(), editorRow.getDocument())) {
//                    return false;
//                }
//            }
//        }
        return someSelection;
    }

    public boolean isEnabledButtonImpl(String buttonId) {
        try {
            switch (buttonId) {
                case "DisplayGroup": {
                    return isListMode();
                }
                case "AdminGroup": {
                    return ((isListMode()
                            || isUpdateMode())
                            && isEnabledButton("ReCalc"))
                            || isEnabledButton("BulkUpdate");
                }
            }
            switch (getModel().getMode()) {
                case READ: {
                    if ("Select".equals(buttonId)) {
                        return isEnabledButton("Remove")
                                || isEnabledButton("Archive");
                    }
                    if ("Refresh".equals(buttonId)) {
                        return true;
                    }
                    if ("Search".equals(buttonId)) {
                        return true;
                    }
                    if ("New".equals(buttonId)) {
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        return e.getShield().isPersistSupported() && e.getShield().isPersistEnabled();
                    }
                    if ("BulkUpdate".equals(buttonId)) {
                        return isEnabledBulkUpdate();
                    }
                    if ("List".equals(buttonId)) {
                        return false;
                    }
                    if ("Remove".equals(buttonId)) {
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        return e.getShield().isRemoveSupported() && e.getShield().isRemoveEnabled();
                    }
                    if ("Advanced".equals(buttonId)) {
                        return isEnabledButton("Archive")
                                || isEnabledButton("ReCalc");
                    }

                    if ("Archive".equals(buttonId)) {
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        return e.getShield().isUpdateSupported() && e.getShield().isUpdateEnabled()
                                && core.isArchivable(getModel().getEntityName());
                    }

                    if ("ReCalc".equals(buttonId)) {
                        return core.isCurrentSessionAdmin();
                    }

                    ActionDialogAdapter e = actionDialogManager.findAction(buttonId);
                    if (e != null) {
                        return e.isEnabled(getEntity().getName(), getModel().getMode(), null);
                    }
                    break;
                }
                case PERSIST: {
                    if ("Save".equals(buttonId)) {
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        return e.getShield().isPersistSupported() && e.getShield().isPersistEnabled();
                    }
                    if ("List".equals(buttonId)) {
                        return true;
                    }
                    ActionDialogAdapter e = actionDialogManager.findAction(buttonId);
                    if (e != null) {
                        return e.isEnabled(getEntity().getName(), getModel().getMode(), getModel().getCurrentDocument());
                    }
                    break;
                }
                case UPDATE: {
                    if ("Save".equals(buttonId)) {
                        UPASecurityManager sm = UPA.getPersistenceUnit().getSecurityManager();
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        boolean allowedUpdate = e.getShield().isUpdateSupported() && e.getShield().isUpdateEnabled();
                        if (!allowedUpdate) {
                            return false;
                        }

                        return sm.isAllowedUpdate(getEntity(), getCurrentId(), getCurrentEntityObject());
                    }
                    if ("New".equals(buttonId)) {
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        return e.getShield().isPersistSupported() && e.getShield().isPersistEnabled();
                    }
                    if ("Refresh".equals(buttonId)) {
                        return true;
                    }
                    if ("List".equals(buttonId)) {
                        return true;
                    }
                    if ("Remove".equals(buttonId)) {
                        Entity e = getEntity();
                        if (e == null) {
                            return false;
                        }
                        return e.getShield().isRemoveSupported() && e.getShield().isRemoveEnabled();
                    }
                    if ("Archive".equals(buttonId)) {
                        return UPA.getPersistenceUnit().getSecurityManager()
                                .isAllowedUpdate(getEntity())
                                && core.isArchivable(getModel().getEntityName());
                    }
                    if ("Advanced".equals(buttonId)) {
                        return isEnabledButton("Archive");
                    }
                    ActionDialogAdapter e = actionDialogManager.findAction(buttonId);
                    if (e != null) {
                        return e.isEnabled(getEntity().getName(), getModel().getMode(), getModel().getCurrentDocument());
                    }
                    break;
                }
                case BULK_UPDATE: {
                    if ("Save".equals(buttonId)) {
                        return isEnabledBulkUpdate();
                    }
                    if ("List".equals(buttonId)) {
                        return true;
                    }
                    if ("Advanced".equals(buttonId)) {
                        return isEnabledButton("Archive");
                    }
                    ActionDialogAdapter e = actionDialogManager.findAction(buttonId);
                    if (e != null) {
                        return e.isEnabled(getEntity().getName(), getModel().getMode(), getModel().getCurrentDocument());
                    }
                    break;
                }
            }
            return false;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
//            throw ex;
            return false;
        }
    }

    public Object getCurrentId() {
        Document selectedObject = getModel().getCurrentDocument();
        EntityBuilder b = getEntity().getBuilder();
        return selectedObject == null ? null : b.documentToId(selectedObject);
    }

    public Object getCurrentEntityObject() {
        Document selectedObject = getModel().getCurrentDocument();
        EntityBuilder b = getEntity().getBuilder();
        return selectedObject == null ? null : b.documentToObject(selectedObject);
    }

    public List getSelectedEntityObjects() {
        List list = new ArrayList();
        EntityBuilder b = getEntity().getBuilder();
        for (Object selectedObject : getModel().getSelectedObjects()) {
            Object o = (selectedObject instanceof Document) ? b.documentToObject((Document) selectedObject) : selectedObject;
            list.add(o);
        }
        return list;
    }

    public String getPreferredExportFileName() {
        return core.getPreferredFileName(VrUPAUtils.getEntityListLabel(getEntity()));
    }

    @Override
    public PModel getModel() {
        return (PModel) super.getModel();
    }

    public String getEntityName() {
        return getModel().getEntityName();
    }

    public VrEditorSearch getSelectedSearch() {
        return core.getEditorSearch(getModel().getSearchType());
    }

    public void resetButtons() {
        synchronized (enabledButtons) {
            enabledButtons.clear();
        }
    }

    public void setEntityName(String entityName) {
        resetButtons();
//        try {
        UPA.getPersistenceUnit().getEntity(entityName);
        getModel().setEntityName(entityName);
        VrEditorSearch y = core.getEditorSearch(getModel().getSearchType());
        getModel().setSearchHelper(y.createHelperString(null, entityName));
        getModel().setList(new ArrayList<EditorRow>());
        getModel().setCurrent(delegated_newInstance());
        getModel().getSearchs().clear();
        getModel().getSearchs().addAll(core.findEntityEditorSearchs(entityName));
//        } catch (RuntimeException ex) {
//            log.log(Level.SEVERE, "Error", ex);
//        }
    }

    public Entity getEntity() {
        return UPA.getPersistenceUnit().getEntity(getModel().getEntityName());
    }

    @Override
    protected void updateMode(VrAccessMode m) {
        resetButtons();
        super.updateMode(m);
        updateModelFromConfig();
        updateView();
    }

    public DynaFormModel getDynModel() {
        return dynModel;
    }

    public void setDynModel(DynaFormModel dynModel) {
        this.dynModel = dynModel;
    }

    public void onReSaveSelection() {
        int count = 0;
        try {
            if (getModel().getMode() == VrAccessMode.READ) {
                for (EditorRow row : getSelectedRows()) {
                    Object id = core.resolveId(getEntityName(), row.getDocument());
                    Document o = core.findDocument(getEntityName(), id);
                    if (o != null) {
                        core.save(getEntityName(), o);
                    }
                    count++;
                }
                reloadPage(true);
            } else {
                onSaveCurrent();
            }
            if (count == 0) {
                FacesUtils.addInfoMessage("Aucun Enregistrement enregistré");
            } else {
                FacesUtils.addInfoMessage(count + " Enregistrement(s) enregistrés(s)");
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(count + " Erreur : " + ex);
        }
    }

    public void onReCalcSelection() {
        int count = 0;
        try {
            if (getModel().getMode() == VrAccessMode.READ) {
                for (EditorRow row : getSelectedRows()) {

                    Object id = core.resolveId(getEntityName(), row.getDocument());
                    if (id != null) {
                        CorePlugin.get().updateObjectValue(getEntityName(), id);
                        count++;
                    }
                }
                reloadPage(true);
            } else {
                onSaveCurrent();
            }
            if (count == 0) {
                FacesUtils.addInfoMessage("Aucun Enregistrement enregistré");
            } else {
                FacesUtils.addInfoMessage(count + " Enregistrement(s) enregistrés(s)");
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(count + " Erreur : " + ex);
        }
    }

    public void onReCalcEntity() {
        try {
            CorePlugin.get().updateEntityFormulas(getEntityName());
            FacesUtils.addInfoMessage("Realcul Réussi");
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onReCalcAll() {
        try {
            CorePlugin.get().updateEntityFormulas(getEntityName());
            FacesUtils.addInfoMessage("Realcul Réussi");
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
        }
    }

    public void onSaveCurrentAndNext() {
        onSaveCurrent();
        onSelectNext();
    }

    public void onSaveCurrentAndPrevious() {
        onSaveCurrent();
        onSelectPrevious();
    }

    public void onSaveCurrentImpl(boolean close) {
        resetButtons();
        try {
            currentViewToModel();
            switch (getModel().getMode()) {
                case PERSIST: {
                    Document c = getModel().getCurrentDocument();
                    core.save(getEntityName(), c);
                    updateMode(VrAccessMode.UPDATE);
                    if (close) {
                        onCancelCurrent();
                    } else {
                        //onReloadCurrent();
                    }
                    break;
                }
                case UPDATE: {
                    Document c = getModel().getCurrentDocument();
                    core.save(getEntityName(), c);
                    if (close) {
                        onCancelCurrent();
                    } else {
                        onReloadCurrent();
                    }
                    break;
                }
                case BULK_UPDATE: {
                    if (hasBulkUpdates()) {
                        for (EditorRow editorRow : getModel().getSelectedRows()) {
                            Document document = editorRow.getDocument();
                            mergeBulkUpdates(document);
                            core.save(getEntityName(), document);
                        }
                        updateMode(VrAccessMode.READ);
                    }
//                    onCancelCurrent();
                    break;
                }
            }
            if (close) {
                reloadPage(true);
                updateMode(VrAccessMode.READ);
            }
//            reloadPage();
            FacesUtils.addInfoMessage("Enregistrement Réussi");
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
//            throw ex;
        }
    }

    public void onSaveCurrent() {
        onSaveCurrentImpl(false);
    }

    public void onSaveCurrentAndClose() {
        onSaveCurrentImpl(true);
    }

    public void onCloneCurrent() {
        resetButtons();
        try {
            currentViewToModel();
            switch (getModel().getMode()) {
                case PERSIST: {
                    Object c = getModel().getCurrentDocument();
                    core.save(getEntityName(), c);
                    updateMode(VrAccessMode.UPDATE);
//                    onCancelCurrent();
                    break;
                }
                case UPDATE: {
                    Document c0 = getModel().getCurrentDocument();
                    Document c = getEntity().getBuilder().createDocument();
                    c.setAll(c0);
                    getModel().setCurrent(createObjRow(c));
                    if (getEntity().getIdFields().size() == 1 && getEntity().getIdFields().get(0).isGeneratedId()) {
                        getEntity().getBuilder().setDocumentId(c, null);
                        core.save(getEntityName(), c);
                    }
//                    onCancelCurrent();
                    break;
                }

            }
//            reloadPage();
            FacesUtils.addInfoMessage("Enregistrement Réussi");
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
//            throw ex;
        }
    }

    public void onDeleteCurrent() {
        resetButtons();
        int count = 0;
        try {
            if (getModel().getMode() == VrAccessMode.READ) {
                for (EditorRow row : getSelectedRows()) {
                    core.remove(getEntityName(), core.resolveId(getEntityName(), row.getDocument()));
                    count++;
                }
                reloadPage(true);
            } else {
                currentViewToModel();
                Object c = getModel().getCurrentDocument();
                core.remove(getEntityName(), core.resolveId(getEntityName(), c));
                count++;
                loadList();
                updateMode(VrAccessMode.READ);
                getModel().setCurrent(null);
//                reloadPage(true);
//                updateMode(AccessMode.READ);
            }
            if (count == 0) {
                FacesUtils.addInfoMessage("Aucun Enregistrement supprimé");
            } else {
                FacesUtils.addInfoMessage(count + " Enregistrement(s) supprimé(s)");
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(count + " Erreur : " + ex);
        }
    }

    public void onArchiveCurrent() {
        resetButtons();
        int count = 0;
        try {
            if (getModel().getMode() == VrAccessMode.READ) {
                for (EditorRow row : getSelectedRows()) {
                    core.archive(getEntityName(), core.resolveId(getEntityName(), row.getDocument()));
                    count++;
                }
                reloadPage(true);
            } else {
                currentViewToModel();
                Object c = getModel().getCurrentDocument();
                core.archive(getEntityName(), core.resolveId(getEntityName(), c));
                getModel().setCurrent(delegated_newInstance());
                updateMode(VrAccessMode.READ);
            }
            if (count == 0) {
                FacesUtils.addInfoMessage("Aucun Enregistrement archivé");
            } else {
                FacesUtils.addInfoMessage(count + " Enregistrement(s) archivé(s)");
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(count + " / Erreur : " + ex);
        }
    }

    @VrOnPageLoad
    public void onPageLoad(String cmd) {
        getAutoFilters().clear();
        getModel().setSearchText(null);
        getModel().setSearchType(null);
        reloadPage(cmd, false);
    }

    protected void reloadPageDocumentEditMode(Document curr, Object eid) {
        if (curr == null) {
            //should not happen though!
            updateMode(VrAccessMode.READ);
            getModel().setCurrent(null);
            return;
        }
        Entity entity = getEntity();
        UPASecurityManager sm = entity.getPersistenceUnit().getSecurityManager();
        EditorRow r = createObjRow(curr);
        //now should try finding row pos
        for (EditorRow filteredObject : getModel().getList()) {
            Object id1 = getEntity().getBuilder().documentToId(filteredObject.getDocument());
            if (id1.equals(eid)) {
                r.setRowPos(filteredObject.getRowPos());
                break;
            }
        }
        getModel().setCurrent(r);
        updateMode(VrAccessMode.UPDATE);
        currentModelToView();
    }

    @Override
    public void reloadPage(String cmd, boolean enableCustomization) {
        resetButtons();
        String oldSearchText = getModel().getSearchText();
        String oldSearchType = getModel().getSearchType();
        EditorFieldSelection oldFieldSelection = getModel().getFieldSelection();
        Set<String> oldDisabledFields = getModel().getDisabledFields();
        String oldEntityName = getModel().getEntityName();
        getModel().setSearchText(null);
        getModel().setSearchType(null);
        getModel().setFieldSelection(null);
        try {
            EditorConfig cfg = VrUtils.parseJSONObject(cmd, EditorConfig.class);
            if (cfg == null) {
                cfg = new EditorConfig();
            }
            getModel().setConfig(cfg);
            getModel().setCmd(cmd);
            setEntityName(cfg.entity);
            if (oldEntityName == null || !oldEntityName.equals(cfg.entity)) {
                oldSearchText = null;
                oldSearchType = null;
                oldFieldSelection = null;
                oldDisabledFields = null;
            }
            if (enableCustomization) {
                getModel().setDisabledFields(oldDisabledFields);
                getModel().setFieldSelection(oldFieldSelection);
                getModel().setSearchType(oldSearchType);
                getModel().setSearchText(oldSearchText);
            } else {
                getModel().setDisabledFields(new HashSet<String>());

                if (cfg.disabledFields != null) {
                    for (String disabledField : cfg.disabledFields) {
                        if (!StringUtils.isBlank(disabledField)) {
                            getModel().getDisabledFields().add(disabledField);
                        }
                    }
                }
                if (cfg.selectedFields != null) {
                    getModel().setFieldSelection(new DefaultEditorFieldSelection(getEntity(), cfg.selectedFields, getModel().getMode(), false));
                } else {
                    getModel().setFieldSelection(new DefaultEditorFieldSelection());
                }
                if (cfg.searchExpr != null || cfg.searchType != null) {
                    getModel().setSearchType(cfg.searchType);
                    getModel().setSearchText(cfg.searchExpr);
                }
            }
            if (cfg.id == null || cfg.id.trim().length() == 0) {
                updateMode(VrAccessMode.READ);
                loadList();
                //if a single row, will autoswitch to edit mode
                if (getModel().getList().size() == 1) {
                    Entity entity = getEntity();
                    Object eid = resolveEntityId(cfg.id);
                    Document curr = getModel().getList().get(0).getDocument();
                    reloadPageDocumentEditMode(curr, eid);
                } else {
                    getModel().setCurrent(null);
                }
            } else if (enableCustomization && getModel().getMode() == VrAccessMode.READ) {
                //do nothing
                updateMode(VrAccessMode.READ);
                loadList();
                getModel().setCurrent(null);
            } else {
                Entity entity = getEntity();
                Object eid = resolveEntityId(cfg.id);
                Document curr = core.findDocument(getEntityName(), eid);
                reloadPageDocumentEditMode(curr, eid);
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            FacesUtils.addErrorMessage(ex);
            throw ex;
        }
    }

    public boolean isEnabledMainPhoto() {
        return core.isEnabledMainPhoto(getEntityName());
    }

    public String getMainName(EditorRow row) {
        String t = getEntity().getMainFieldValue(row.getDocument());
        if (StringUtils.isBlank(t)) {
            t = "NoName";
        }
        return t;
    }

    public String getMainPhoto(EditorRow row) {
        return core.getMainPhotoPath(getEntityName(), getEntity().getBuilder().documentToId(row.getDocument()), row.getDocument());
    }

    public String getMainIcon(EditorRow row) {
        return core.getMainIconPath(getEntityName(), getEntity().getBuilder().documentToId(row.getDocument()), row.getDocument());
    }

    public void loadList() {
        String _listFilter = getModel().getConfig().listFilter;
        if (!StringUtils.isBlank(_listFilter)) {
            _listFilter = "(" + _listFilter + ")";
        } else {
            _listFilter = "";
        }
        HashMap<String, Object> parameters = new HashMap<>();
        int autoFilterIndex = 1;
        for (AutoFilter o : getAutoFilters()) {
            String filterExpression = o.createFilterExpression(parameters, "af" + autoFilterIndex);
            if (!StringUtils.isBlank(filterExpression)) {
                if (!StringUtils.isBlank(_listFilter)) {
                    _listFilter += " and ";
                }
                _listFilter += "(" + filterExpression + ")";
            }
            autoFilterIndex++;
        }
        List<Document> found = core.findDocumentsByFilter(getEntityName(), _listFilter, getModel().getSearchType(), getModel().getSearchText(), parameters);
        List<EditorRow> filteredObjects = new ArrayList<>();
        Entity entity = getEntity();
        UPASecurityManager sm = entity.getPersistenceUnit().getSecurityManager();
        EntityBuilder b = entity.getBuilder();
        for (Document rec : found) {
            Object id = b.documentToId(rec);
            if (sm.isAllowedNavigate(entity, id, rec)) {
                EditorRow row = createObjRow(rec);
                row.setRowPos(filteredObjects.size());
                filteredObjects.add(row);
            }
        }
        getModel().setList(filteredObjects);
    }

    private Object resolveEntityId(String strId) {
        if (strId == null || strId.trim().length() == 0) {
            return null;
        }
        //dont know why!
        if (strId.equals("null")) {
            return null;
        }
        try {
            return Convert.toInt(strId);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getSortLayoutPropertyValue(Object o, String property) {
        return getLayoutPropertyValue(o, property);
    }

    public String getRowStyle() {
        Entity p = getEntity();
        String v = p.getProperties().getString(UIConstants.Grid.ROW_STYLE);
        if (StringUtils.isBlank(v)) {
            return v;
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        String val = null;

        try {
            ELContext elContext = fc.getELContext();
            String expr = "${" + v + "}";
            ValueExpression targetExpression = fc.getApplication().getExpressionFactory().createValueExpression(elContext, expr, String.class);
            val = (String) targetExpression.getValue(elContext);
//            val = (String) elContext.getELResolver().getValue(elContext, null, v);
        } catch (RuntimeException e) {
            throw new FacesException(e.getMessage(), e);
        }

        return val;
    }

    public Object getPropertyColumnStyleClass(String property, EditorRow row) {
        return getPropertyColumnCodeValue(UIConstants.Grid.COLUMN_STYLE_CLASS, property, row);
    }

    public Object getPropertyColumnStyle(String property, EditorRow row) {
        return getPropertyColumnCodeValue(UIConstants.Grid.COLUMN_STYLE, property, row);
    }

    protected Object getPropertyColumnCodeValue(String propertyCodeName, String property, EditorRow row) {
        Field p = getEntity().getField(property);
        String v = p.getProperties().getString(propertyCodeName);
        if (v != null) {
            v = v.trim();
            if (v.startsWith("#{") && v.endsWith("}")) {
                VrColorTable table = VrApp.getBean(VrColorTable.class);
                PersistenceUnit pu = UPA.getPersistenceUnit();
                Map<String, Object> map = new HashMap<>();
                map.put("this", row == null ? null : row.getDocument());
                Document r = pu.getFactory().createObject(Document.class);
                r.setObject("pos", row == null ? -1 : row.getRowPos());
                map.put("ui", r);
                String expr = v.substring(2, v.length() - 1);
                ExpressionManager expressionManager = pu.getExpressionManager();
                if (!expressionManager.containsFunction("inthash")) {
                    expressionManager.addFunction("inthash", DataTypeFactory.INT, inthash);
                }
                if (!expressionManager.containsFunction("hashToStringArr")) {
                    expressionManager.addFunction("hashToStringArr", DataTypeFactory.STRING, hashToStringArr);
                }
                //Should remove me some how, but ii'm been used!
                if (!expressionManager.containsFunction("hashCssColor")) {
                    expressionManager.addFunction("hashCssColor", DataTypeFactory.STRING, hashCssColor);
                }
                Expression expression = expressionManager.simplifyExpression(expr, map);
                QLEvaluator evaluator = expressionManager.createEvaluator();
                evaluator.getRegistry().registerFunctionEvaluator("hashToStringArr", hashToStringArr);
                evaluator.getRegistry().registerFunctionEvaluator("inthash", inthash);
                evaluator.getRegistry().registerFunctionEvaluator("hashCssColor", hashCssColor);
                expression = evaluator.evalObject(expression, null);
                if (expression instanceof Literal) {
                    Object t = ((Literal) expression).getValue();
                    String ret = t == null ? "" : t.toString().trim();
                    for (int i = 0; i < 20; i++) {
                        ret = ret.replace("{bgcolor" + i + "}", table.getBgColor(i));
                        ret = ret.replace("{fgcolor" + i + "}", table.getFgColor(i));
                    }
                    return ret;
                }
            }
        }
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
            Entity e = getEntity();
            Field field = e.getField(property);
            Object v = field.getMainValue(o);

            if (v != null) {
                DataType d = field.getDataType();
                if (d instanceof EnumType) {
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
        return core.getObjectName(getEntityName(), obj);
    }

    protected Document createInitializedDocument() {
        return getEntity().getBuilder().createInitializedDocument();
    }

    @Override
    protected EditorRow delegated_newInstance() {
        resetButtons();
        try {
            if (getModel().getEntityName() != null) {
                Entity e = getEntity();
                Document o = createInitializedDocument();
                for (Field field : e.getFields()) {
                    Object v = field.getDefaultValue();
                    if (v != null) {
                        o.setObject(field.getName(), v);
//                        e.getBuilder().setProperty(o, field.getName(), v);
                    }
                }
                EntityBuilder b = e.getBuilder();
                EditorRow r = createObjRow(o);
                r.setRead(true);
                r.setWrite(true);
                r.setSelected(false);
                return r;
            }
            EditorRow r = createObjRow(createInitializedDocument());
            return r;
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    public List<ColumnView> getColumns() {
        return columns;
    }

    public void updateModelFromConfig() {
        resetButtons();
        EditorConfig cfg = VrUtils.parseJSONObject(getModel().getCmd(), EditorConfig.class);
        Object o = getModel().getCurrentDocument();
        Entity e = getEntity();
        EntityBuilder builder = e.getBuilder();
        if (cfg != null && cfg.values != null && !cfg.values.isEmpty() && o != null && e != null) {
            for (Map.Entry<String, String> entrySet : cfg.values.entrySet()) {
                String fieldName = entrySet.getKey();
                String fieldValueString = entrySet.getValue();
                Field field = e.findField(fieldName);
                if (field != null) {
                    Object value = VrUPAUtils.jsonToObj(fieldValueString, field.getDataType());
                    builder.setProperty(o, fieldName, value);
                    Relationship manyToOnePrimitiveRelationShip = VrUPAUtils.getManyToOnePrimitiveRelationShip(field);
                    if (manyToOnePrimitiveRelationShip != null) {
                        //this is an id of another field
                        Field entityField = manyToOnePrimitiveRelationShip.getSourceRole().getEntityField();
                        Entity re = manyToOnePrimitiveRelationShip.getTargetEntity();
                        Object v = VrUPAUtils.findById2(re, value);
//                        v=re.getBuilder().objectToDocument(v);
                        builder.setProperty(o, entityField.getName(), v);
                    }
                }
            }
        }
    }

    public List<EditorActionConfig> getActionsByType(String... types) {
        HashSet<String> acceptable = new HashSet<>(Arrays.asList(types));
        List<EditorActionConfig> ok = new ArrayList<>();
        for (EditorActionConfig action : getModel().getActions()) {
            if (acceptable.contains(action.getType())) {
                ok.add(action);
            }
        }
        return ok;
    }

    public void currentModelToView() {
        List<EditorActionConfig> act = new ArrayList<>();
        for (ActionDialogAdapter a : actionDialogManager.findActionsByEntity(getModel().getEntityName())) {
            if (a.isEnabled(getEntity().getName(), getModel().getMode(), getModel().getCurrentDocument())) {
                act.add(new EditorActionConfig(
                        a.isDialog() ? "dialog"
                        : a.isInvoke() ? "invoke"
                        : a.isGoto() ? "goto"
                        : "unknown",
                        a.getLabel(),
                        a.getDescription(),
                        a.getIcon(),
                        a.getId(),
                        a.getCommand(getSelectedIdStrings())
                ));
            }
        }
//        for (ActionInfo a : objService.getEntityActionList(getModel().getEntityName(), getModel().getCurrentObj())) {
//            act.add(new ObjFormAction(a.getLabel(), a.getStyle(), a.getId()));
//        }
        getModel().setActions(act);
        try {
            Object currentObj = getModel().getCurrentDocument();
            for (PropertyView property : properties) {
                property.loadFrom(currentObj);
                property.refresh();
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
            Document document = getModel().getCurrentDocument();
            for (PropertyView property : properties) {
                property.storeTo(document);
            }
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
    }

    private boolean updateViewFlag;

    public void updateView() {
        if (updateViewFlag) {
            return;
        }
        updateViewFlag = true;
        try {
        resetButtons();
            try {
                //reset table state
                FacesContext currentInstance = FacesContext.getCurrentInstance();
                try {
                    if (currentInstance != null) {
                        UIComponent table = currentInstance.getViewRoot().findComponent(":listForm:listTable");
                        if (table != null) {
                            table.setValueExpression("sortBy", null);
                        }
                    }
                } catch (Exception ex) {
                    //ignore
                }
                Entity entity = getEntity();
                try {
                    Vr.get().getTitleCrumb().setTitle(getPageTitleString(entity, getModel().getMode()));
                    if (currentInstance != null) {
                        UIComponent table = currentInstance.getViewRoot().findComponent(":listForm:listTable");
                        if (table != null) {
                            table.setValueExpression("sortBy", null);
                        }
                    }
                } catch (Exception ex) {
                    //ignore
                }
                //update columns
                Entity ot = UPA.getPersistenceUnit().getEntity(getEntityName());
                columns.clear();
                properties.clear();
                boolean adm = core.isCurrentSessionAdmin();

                List<Field> fields = new ArrayList<Field>();
                if (getModel().getFieldSelection() == null) {
                    fields = new ArrayList<>(ot.getFields(FieldFilters.byModifiersAnyOf(FieldModifier.MAIN, FieldModifier.SUMMARY)));
                } else {
                    if (getModel().getFieldSelection().getEntity() == null) {
                        getModel().getFieldSelection().prepare(getEntity(), getModel().getMode());
                    }
                    fields = getModel().getFieldSelection().getVisibleFields();
                }
//                VrUtils.sortPreserveIndex(fields, new Comparator<Field>() {
//                    @Override
//                    public int compare(Field o1, Field o2) {
////                    return Integer.compare(o1.getPreferredIndex(),o2.getPreferredIndex());
//                        return 0;
//                    }
//                });

                VrAccessMode mode = getModel().getMode();
                for (Field field : fields) {
                    AccessLevel ral = field.getEffectiveAccessLevel(mode.toUpaMode());
                    boolean readonly = false;
                    if (ral == AccessLevel.INACCESSIBLE) {
                        continue;
                    } else if (ral == AccessLevel.READ_ONLY) {
                        readonly = true;
                    }
                    String type = UIConstants.Control.TEXT;
                    if (PlatformUtils.isBooleanType(field.getDataType().getPlatformType())) {
                        type = UIConstants.Control.CHECKBOX;
                    }
                    String property = field.getName();
                    String propertyExpr = field.getName();
                    if (field.getDataType() instanceof ManyToOneType) {
                        ManyToOneType e = (ManyToOneType) field.getDataType();
                        propertyExpr += "." + e.getRelationship().getTargetEntity().getMainField().getName();
                    }
                    columns.add(new ColumnView(field.getTitle(), property, propertyExpr, type));
                }
                FormHelper h = new FormHelper();
                dynModel = h.dynModel;

                ViewContext viewContext = new ViewContext();
                List<OrderedPropertyView> propertyViews = new ArrayList<OrderedPropertyView>();
                List<EntityItem> entityParts = ot.getItems();
//                List<EntityPart> entityParts = VrUtils.sortPreserveIndex(ot.getItems(), new Comparator<EntityPart>() {
//                    @Override
//                    public int compare(EntityItem o1, EntityItem o2) {
////                    if (o1 instanceof Section && o2 instanceof Section) {
////                        return 0;
////                    }
////                    if (o1 instanceof Section && o2 instanceof Field) {
////                        return 1;
////                    }
////                    if (o1 instanceof Field && o2 instanceof Section) {
////                        return -1;
////                    }
////                    if (o1 instanceof Field && o2 instanceof Field) {
////                        return Integer.compare(((Field) o1).getPreferredIndex(), ((Field) o2).getPreferredIndex());
////                    }
//                        return 0;
//                    }
//                });
                boolean saveAllowed = isSaveAllowed();
                if (!saveAllowed) {
                    viewContext.getProperties().put("enabled", false);
                }
                for (EntityItem entityPart : entityParts) {
                    if (entityPart instanceof Section) {
                        List<Field> sectionFields = ((Section) entityPart).getFields(FieldFilters.byModifiersNoneOf(FieldModifier.SYSTEM));
                        List<Field> sectionSortedFields = VrUtils.sortPreserveIndex(sectionFields, new Comparator<Field>() {
                            @Override
                            public int compare(Field o1, Field o2) {
//                    return Integer.compare(o1.getPreferredIndex(),o2.getPreferredIndex());
                                return 0;
                            }
                        });

                        for (Field field : sectionSortedFields) {
                            updateViewForField(field, viewContext, propertyViews);
                        }

                    } else {
                        Field field = (Field) entityPart;
                        updateViewForField(field, viewContext, propertyViews);
                    }
                }
//            for (Field field : VrUtils.sortPreserveIndex(new ArrayList<>(ot.getFields(FieldFilters.byModifiersNoneOf(FieldModifier.SYSTEM))), Comparator.comparingInt(Field::getPreferredIndex))) {
//                Map<String, Object> config = new HashMap<>();
//                if (getModel().getDisabledFields().contains(field.getName())) {
//                    config.put("disabled", true);
//                }
//                PropertyView[] ctrls = propertyViewManager.createPropertyViews(field.getName(), field, config, viewContext);
//                if (ctrls != null) {
//                    for (PropertyView ctrl : ctrls) {
//                        if (ctrl != null) {
//                            propertyViews.add(new OrderedPropertyView(propertyViews.size(), ctrl));
//                        }
//                    }
//                }
//            }
                if (getModel().getMode() == VrAccessMode.UPDATE) {
                    boolean firstDetailRelation = true;
                    int counter = 0;
                    int maxPerLine = 2;
                    for (Relationship relation : ot.getRelationships()) {
                        if (relation.getTargetEntity().getName().equals(ot.getName())) {
                            if (!core.isInaccessibleEntity(relation.getSourceRole().getEntity().getName())) {
                                if (relation.getRelationshipType() == RelationshipType.COMPOSITION) {
                                    EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.Control.ENTITY_DETAIL, propertyViewManager);
                                    details.setPrependNewLine((counter % maxPerLine) == 0);
                                    details.setAppendNewLine(false);
                                    details.setColspan(1);
                                    details.setDisabled(!UPA.getPersistenceUnit().getSecurityManager().isAllowedNavigate(relation.getSourceEntity()));
                                    counter++;
                                    if (firstDetailRelation) {
                                        details.setSeparatorText("Details");
                                        firstDetailRelation = false;
                                    }
                                    propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                                }
                            }
                        }
                    }
                    counter = 0;
                    for (Relationship relation : ot.getRelationships()) {
                        if (relation.getTargetEntity().getName().equals(ot.getName())) {
                            if (!core.isInaccessibleEntity(relation.getSourceRole().getEntity().getName())) {
                                if (relation.getRelationshipType() == RelationshipType.AGGREGATION) {
                                    EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.Control.ENTITY_DETAIL, propertyViewManager);
                                    details.setPrependNewLine((counter % maxPerLine) == 0);
                                    details.setAppendNewLine(false);
                                    details.setColspan(1);
                                    details.setDisabled(!UPA.getPersistenceUnit().getSecurityManager().isAllowedNavigate(relation.getSourceEntity()));
                                    counter++;
                                    if (firstDetailRelation) {
                                        details.setSeparatorText("Références");
                                        firstDetailRelation = false;
                                    }
                                    propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                                }
                            }
                        }
                    }
                    counter = 0;
                    boolean firstAssoRelation = true;
                    for (Relationship relation : ot.getRelationships()) {
                        if (relation.getTargetEntity().getName().equals(ot.getName())) {
                            if (!core.isInaccessibleEntity(relation.getSourceRole().getEntity().getName())) {
                                final RelationshipType t = relation.getRelationshipType();
                                if (t != RelationshipType.AGGREGATION && t != RelationshipType.COMPOSITION) {
                                    EntityDetailPropertyView details = new EntityDetailPropertyView(relation.getName(), relation, UIConstants.Control.ENTITY_DETAIL, propertyViewManager);
                                    details.setPrependNewLine((counter % maxPerLine) == 0);
                                    details.setAppendNewLine(false);
                                    details.setDisabled(!UPA.getPersistenceUnit().getSecurityManager().isAllowedNavigate(relation.getSourceEntity()));
                                    details.setColspan(1);
                                    counter++;
                                    if (firstAssoRelation) {
                                        details.setSeparatorText("Autres Références");
                                        firstAssoRelation = false;
                                    }
                                    propertyViews.add(new OrderedPropertyView(propertyViews.size(), details));
                                }
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
                    LabelPropertyView label = null;
                    for (int i = 0; i < ctrl.getPrependEmptyCells(); i++) {
                        h.addLabel("", 1, 1);
                    }
                    if (hasSeparator) {
                        h.addControl(new SeparatorPropertyView(ctrl.getSeparatorText()), UIConstants.Control.SEPARATOR, Integer.MAX_VALUE, 1);
                    }
                    if (!ctrl.isNoLabel()) {
                        //label = h.addLabel(ctrl.getName(), 1, 1);
                        h.addControl(label = new LabelPropertyView(ctrl.getName()), UIConstants.Control.LABEL, 1, 1);
                        if (ctrl.isLabelNewLine()) {
                            h.newLine();
                        }
                        for (int i = 0; i < ctrl.getAppendEmptyCells(); i++) {
                            h.addLabel("", 1, 1);
                        }
                    }

                    DynaFormControl control12 = h.addControl(ctrl, ctrl.getCtrlType(), ctrl.getColspan(), ctrl.getRowpan());
                    if (label != null) {
                        label.setForControl(ctrl);
                        if (ctrl.isDisabled()) {
                            label.setDisabled(true);
                        }
                        if (!ctrl.isVisible()) {
                            label.setVisible(false);
                        }
                    }
                    if (ctrl.isAppendNewLine()) {
                        h.newLine();
                    }

                }

                List<AutoFilter> newAutoFilters = new ArrayList<>();
                for (AutoFilterData autoFilterData : core.getEntityAutoFilters(entity.getName())) {
                    String type = autoFilterData.getFilterType();
                    if (StringUtils.isBlank(type) || type.equals("single-selection")) {
                        AutoFilter autoFilter0 = null;
                        for (AutoFilter autoFilter : autoFilters) {
                            if (autoFilter.getData().equals(autoFilterData)) {
                                autoFilter0 = autoFilter;
                                break;
                            }
                        }
                        if (autoFilter0 != null) {
                            newAutoFilters.add(autoFilter0);
                        } else {
                            EditorConfig config = getModel().getConfig();
                            boolean autoSelect = (config == null || !config.ignoreAutoFilter);
                            newAutoFilters.add(new SingleSelectionAutoFilter(autoFilterData, autoSelect));
                        }
                    } else {
                        throw new IllegalArgumentException("Unsupported Auto Filter Type " + type);
                    }
                }
                autoFilters.clear();
                autoFilters.addAll(newAutoFilters);

                currentModelToView();
            } catch (RuntimeException ex) {
                log.log(Level.SEVERE, "Error", ex);
                throw ex;
            }
        } finally {
            updateViewFlag = false;
        }
    }

    private void updateViewForField(Field field, ViewContext viewContext, List<OrderedPropertyView> propertyViews) {
        Document currentDocument = getModel().getCurrentDocument();
        Map<String, Object> config = new HashMap<>();
        boolean enabled0 = true;
        if (viewContext.getProperties().get("enabled") != null && !((Boolean) viewContext.getProperties().get("enabled"))) {
            enabled0 = false;
        }
        if (getModel().getDisabledFields().contains(field.getName()) || !enabled0) {
            config.put("enabled", false);
        } else {
            String cond = UPAObjectHelper.findStringProperty(field, UIConstants.Form.ENABLED_CONDITION, null, null);
            if (!StringUtils.isBlank(cond)) {
                JavascriptEvaluator jse = new JavascriptEvaluator(cond);
                boolean enabled = (boolean) jse.eval(currentDocument);
                config.put("enabled", enabled && enabled0);
            }
        }
        {
            String cond = UPAObjectHelper.findStringProperty(field, UIConstants.Form.VISIBLE_CONDITION, null, null);
            if (!StringUtils.isBlank(cond)) {
                JavascriptEvaluator jse = new JavascriptEvaluator(cond);
                boolean visible = (boolean) jse.eval(currentDocument);
                config.put("visible", visible);
            }
        }
        {
            String cond = UPAObjectHelper.findStringProperty(field, UIConstants.Form.SUBMIT_ON_CHANGE, null, null);
            if (!StringUtils.isBlank(cond)) {
                JavascriptEvaluator jse = new JavascriptEvaluator(cond);
                boolean submitOnChange = (boolean) jse.eval(currentDocument);
                config.put("submitOnChange", submitOnChange);
            }
        }
        PropertyView[] ctrls = propertyViewManager.createPropertyViews(field.getName(), field, config, viewContext);
        if (ctrls != null) {
            for (PropertyView ctrl : ctrls) {
                if (ctrl != null) {
                    propertyViews.add(new OrderedPropertyView(propertyViews.size(), ctrl));
                }
            }
        }
    }

    public void updatePropertyViews() {
        boolean forceDisabled = false;
        Entity e = getEntity();
        if (e == null) {
            forceDisabled = true;
        } else {
            switch (getModel().getMode()) {
                case PERSIST: {
                    forceDisabled = e.getShield().isPersistSupported() && e.getShield().isPersistEnabled();
                    break;
                }
                case UPDATE: {
                    forceDisabled = e.getShield().isUpdateSupported() && e.getShield().isUpdateEnabled();
                    break;
                }
            }
        }
        for (PropertyView propertyView : getProperties()) {
            updatePropertyView(propertyView, forceDisabled);
        }
    }

    private void updatePropertyView(PropertyView p, boolean forceDisabled) {
        Document currentDocument = getModel().getCurrentDocument();
        if (p instanceof FieldPropertyView) {
            Field field = ((FieldPropertyView) p).getField();
            if (forceDisabled) {
                p.setDisabled(true);
            } else {
                if (getModel().getDisabledFields().contains(field.getName())) {
                    p.setDisabled(true);
                } else {
                    String cond = UPAObjectHelper.findStringProperty(field, UIConstants.Form.ENABLED_CONDITION, null, null);
                    if (!StringUtils.isBlank(cond)) {
                        JavascriptEvaluator jse = new JavascriptEvaluator(cond);
                        boolean enabled = (boolean) jse.eval(currentDocument);
                        p.setDisabled(!enabled);
                    }
                }
            }

            String cond = UPAObjectHelper.findStringProperty(field, UIConstants.Form.VISIBLE_CONDITION, null, null);
            if (!StringUtils.isBlank(cond)) {
                JavascriptEvaluator jse = new JavascriptEvaluator(cond);
                boolean visible = (boolean) jse.eval(currentDocument);
                p.setVisible(visible);
            }

        }

    }

    public NamedId getEntityAutoFilterDefaultSelectedValue(AutoFilterData autoFilterData, DataType filterType) {
        EditorConfig config = getModel().getConfig();
        if (config != null && config.ignoreAutoFilter) {
            return null;
        }
        return autoFilterData.getDefaultSelectedValue();
    }

    public void updateSearchType(String s) {
        getModel().setSearchType(core.getEditorSearch(s).getId());
    }

    public void onAutoFilterChange() {
        onRefresh();
    }

    public List<AutoFilter> getAutoFilters() {
        return autoFilters;
    }

    public boolean isEnabledSelectNext() {
        switch (getModel().getMode()) {
            case DEFAULT:
            case PERSIST:
            case BULK_UPDATE:
            case READ: {
                return false;
            }
        }
        EditorRow objRow = getModel().getCurrent();
        return objRow != null && objRow.getRowPos() >= 0 && objRow.getRowPos() + 1 < getModel().getList().size();
    }

    public void onSelectNext() {
        EditorRow objRow = getModel().getCurrent();
        if (objRow != null && objRow.getRowPos() >= 0 && objRow.getRowPos() + 1 < getModel().getList().size()) {
            getModel().setCurrent(getModel().getList().get(objRow.getRowPos() + 1));
            onSelectCurrent();
        }
    }

    public String getNavigateLabel() {
        switch (getModel().getMode()) {
            case BULK_UPDATE: {
                return "" + getModel().getSelectedRows().size();
            }
        }
        return (getModel().getCurrent().getRowPos() + 1) + "/" + getModel().getList().size();
    }

    public boolean isEnabledNavigate() {
        switch (getModel().getMode()) {
            case DEFAULT:
            case PERSIST:
            case READ: {
                return false;
            }
            case BULK_UPDATE: {
                return true;
            }
        }
        EditorRow objRow = getModel().getCurrent();
        return objRow != null && objRow.getRowPos() >= 0 && getModel().getList().size() > 0;
    }

    public boolean isEnabledSelectPrevious() {
        switch (getModel().getMode()) {
            case DEFAULT:
            case PERSIST:
            case BULK_UPDATE:
            case READ: {
                return false;
            }
        }
        EditorRow objRow = getModel().getCurrent();
        return objRow != null && objRow.getRowPos() > 0 && getModel().getList().size() > 0;
    }

    public void onSelectPrevious() {
        EditorRow objRow = getModel().getCurrent();
        if (objRow != null && objRow.getRowPos() > 0 && getModel().getList().size() > 0) {
            getModel().setCurrent(getModel().getList().get(objRow.getRowPos() - 1));
            onSelectCurrent();
        }
    }

    public void onRefreshCurrent() {
        if (getModel().getMode() == VrAccessMode.READ) {
            reloadPage(true);
        } else {
            onReloadCurrent();
        }
    }

    public void onReloadCurrent() {
        switch (getModel().getMode()) {
            case PERSIST: {
                onNew();
                break;
            }
            case UPDATE: {
                Object id = getCurrentId();
                EditorRow c = getModel().getCurrent();
                Document curr = id == null ? null : core.findDocument(getEntityName(), id);
                EditorRow objRow = createObjRow(curr);
                if (c != null) {
                    objRow.setRowPos(c.getRowPos());
                }
                onSelect(objRow);
                break;
            }
        }
    }

    public String idToString(Object id) {
        return id == null ? null : String.valueOf(id);
    }

    public EditorRow createObjRow(Document o) {
        Entity e = getEntity();
        EntityBuilder b = e.getBuilder();
        Object id = b.documentToId(o);
        EditorRow r = new EditorRow(idToString(id), o, b.documentToObject(o));
        UPASecurityManager sm = e.getPersistenceUnit().getSecurityManager();
        r.setRead(sm.isAllowedLoad(e, id, o));
        r.setWrite(sm.isAllowedUpdate(e, id, o));
        r.setRowPos(-1);
        return r;
    }

    @Override
    public void onSelectCurrent() {
        resetButtons();
        try {
            super.onSelectCurrent();
            currentModelToView();
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
        VrMenuManager menu = VrApp.getBean(VrMenuManager.class);
        if (getModel().getCurrent() != null) {
            EditorConfig cfg = new EditorConfig();
            cfg.entity = getEntityName();
            Object curr = getModel().getCurrent() == null ? null : getEntity().getBuilder().objectToId(getModel().getCurrent().getDocument());
            cfg.id = curr == null ? null : String.valueOf(curr);
            if (getModel().getFieldSelection() != null) {
                ArrayList<String> all = new ArrayList<>();
                for (Field f : getModel().getFieldSelection().getVisibleFields()) {
                    all.add(f.getName());
                }
                cfg.selectedFields = all.toArray(new String[all.size()]);
            }

            cfg.searchExpr = getModel().getSearchText();
            cfg.searchType = getModel().getSearchType();

            //will no more add to history the same entity editor (for the same entity name)
            VrPageHistoryItem pi = menu.peekHistory();
            if (pi != null && ("editor".equals(pi.getCommand()) || "editorCtrl".equals(pi.getCommand()))) {
                EditorConfig c = VrUtils.parseJSONObject(pi.getArguments(), EditorConfig.class);
                if (c != null) {
                    if (c.entity != null && c.entity.equals(cfg.entity)) {
                        menu.popHistory();
                    }
                }
            }
            menu.pushHistory("editor", cfg);
        }
    }

    public void proceedCancelActionDialog() {
        DialogBuilder.closeCurrent();
    }

    public void fireEventSearchCancelled() {
        DialogBuilder.closeCurrent();
    }

//    public void updateColumnSelection() {
//        if (getModel().getFieldSelection() != null) {
//            getModel().getFieldSelection().save();
//        }
//        updateView();
//        fireEventSearchClosed();
//    }
    public void fireEventClearSelection() {
        onClearFieldSelection();
        DialogBuilder.closeCurrent();
    }

    public void fireEventSelectionValidated() {
        if (getModel().getFieldSelection() != null) {
            getModel().getFieldSelection().save();
        }
        updateView();
        DialogBuilder.closeCurrent();

    }

    public void fireEventSearchClosed() {
//        RequestContext ctx = RequestContext.getCurrentInstance();
        //Object obj
//        ctx.closeDialog(new DialogResult(null, null));
        DialogBuilder.closeCurrent();
//        String[] ids=new String[]{"listForm:listTable"};
//        ctx.update(Arrays.asList(ids));
//        ctx.execute("vpcdoit();");
    }

    public void onSimpleSearch() {
        new DialogBuilder("/modules/editor/search-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                .open();
    }

    public void onSimpleFieldSelection() {
        EditorFieldSelection oldSearch = getModel().getFieldSelection();
        if (oldSearch == null) {
            oldSearch = new DefaultEditorFieldSelection();
        }
        getModel().setFieldSelection(oldSearch);
        oldSearch.prepare(getEntity(), getModel().getMode());
        new DialogBuilder("/modules/editor/fields-dialog")
                .setResizable(true)
                .setDraggable(true)
                .setModal(true)
                //                .setHeight(500)
                .open();
    }

    public void onDoNothing() {
//        System.out.println("do nothing");
    }

    public void onClearSearch() {
        getModel().setSearchType(null);
        getModel().setSearchText(null);
        onRefresh();
    }

    public void onClearFieldSelection() {
        if (getModel().getFieldSelection() != null) {
            getModel().getFieldSelection().reset();
        }
        onRefresh();
    }

    public void onList() {
        resetButtons();
        try {
            getModel().setCurrent(null);//delegated_newInstance()
            updateMode(VrAccessMode.READ);
        } catch (RuntimeException ex) {
            log.log(Level.SEVERE, "Error", ex);
            throw ex;
        }
        loadList();
        pushHistory();
    }

    private void pushHistory() {
        VrMenuManager menu = VrApp.getBean(VrMenuManager.class);
        EditorRow current = getModel().getCurrent();
        EditorConfig cfg = new EditorConfig();
        cfg.entity = getEntityName();
        cfg.id = current == null ? null : String.valueOf(getEntity().getBuilder().objectToId(current.getDocument()));
        cfg.values = getModel().getConfig().values;
        cfg.listFilter = getModel().getConfig().listFilter;
        cfg.disabledFields = getModel().getConfig().disabledFields;
        menu.pushHistory("editor", cfg);
    }

    @Override
    public void onCancelCurrent() {
        resetButtons();
        super.onCancelCurrent();
        pushHistory();
    }

    public List<PropertyView> getProperties() {
        return properties;
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
        resetButtons();
        if ("Persist".equals(actionKey)) {
            onNew();
        } else if ("Save".equals(actionKey)) {
            onSaveCurrent();
        } else if ("Remove".equals(actionKey)) {
            onDeleteCurrent();
        } else {
            try {
                currentViewToModel();
                Object c = getModel().getCurrentDocument();
                ActionDialogAdapter act = actionDialogManager.findAction(actionKey);
                if (act != null) {
                    ActionDialogResult rr = act.invoke(getEntity().getName(), c, getSelectedIdStrings(), args);
                    applyActionDialogResult(rr);
                }
            } catch (RuntimeException ex) {
                log.log(Level.SEVERE, "Error", ex);
                throw ex;
            }
        }
    }

    private void applyActionDialogResult(ActionDialogResult rr) {
        if (rr != null) {
            ActionDialogResultPostProcess r = rr.getType();
            String message = rr.getMessage();
            if (message != null) {
                FacesUtils.addInfoMessage(message);
            }
            if (r != null) {
                switch (r) {
                    case RELOAD_CURRENT: {
                        if (getModel().getMode() == VrAccessMode.READ) {
                            reloadPage(true);
                        } else {
                            onReloadCurrent();
                        }
                        break;
                    }
                    case RELOAD_ALL: {
                        if (getModel().getMode() == VrAccessMode.READ) {
                            reloadPage(true);
                        } else {
                            loadList();
                            updateMode(VrAccessMode.READ);
                            getModel().setCurrent(null);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void updateAllFormulas() {
        resetButtons();
        UPA.getPersistenceUnit().updateAllFormulas();
    }

    public PropertyView findPropertyView(String componentId) {
        for (PropertyView p : properties) {
            if (p.getComponentId().equals(componentId)) {
                return p;
            }
        }
        return null;
    }

    public List<EditorRow> getSelectedRows() {
        List<EditorRow> selected = new ArrayList<>();
        if (isCustomCheckboxSelection()) {
            for (EditorRow r : getModel().getList()) {
                if (r.isSelected()) {
                    selected.add(r);
                }
            }
        } else {
            selected.addAll(getModel().getSelectedRows());
        }
        return selected;
    }

    public List<String> getSelectedIdStrings() {
        List<String> selected = new ArrayList<>();
        if (getModel().getMode() == VrAccessMode.READ) {
            for (EditorRow r : getSelectedRows()) {
                String iid = StringUtils.nonNull(core.resolveId(getEntityName(), r.getDocument()));
                if (iid != null) {
                    selected.add(iid);
                } else {
                    System.out.println("Why?");
                }
            }
        } else if (getModel().getCurrentDocument() != null) {
            String iid = StringUtils.nonNull(core.resolveId(getEntityName(), getModel().getCurrentDocument()));
            if (iid != null) {
                selected.add(iid);
            }
        }
        return selected;
    }

    public void proceedOpenActionDialog() {
        String actionId = getModel().getActionId();
        if (actionId != null) {
            ActionDialogAdapter ed = VrApp.getBean(ActionDialogManager.class).findAction(actionId);
            if (ed != null) {
                if (ed.isDialog()) {

                    currentViewToModel();
                    ed.openDialog(getSelectedIdStrings());
                    return;
                } else if (ed.isInvoke()) {
                    try {
                        PActionParam[] params = getModel().getParams();
                        Object[] args = new Object[params.length];
                        for (int i = 0; i < params.length; i++) {
                            switch (params[i].getType()) {
                                case STRING: {
                                    args[i] = params[i].getSvalue();
                                    break;
                                }
                                case INT: {
                                    args[i] = Convert.toInt(params[i].getSvalue(), IntegerParserConfig.STRICT);
                                    break;
                                }
                                case DOUBLE: {
                                    args[i] = Convert.toDouble(params[i].getSvalue(), DoubleParserConfig.STRICT);
                                    break;
                                }
                                default: {
                                    throw new IllegalArgumentException("Unsupported");
                                }
                            }
                        }
                        currentViewToModel();
                        Object c = getModel().getCurrentDocument();
                        ActionDialogResult rr = ed.invoke(getEntity().getName(), c, getSelectedIdStrings(), args);
                        applyActionDialogResult(rr);
                        DialogBuilder.closeCurrent();
                    } catch (RuntimeException ex) {
                        FacesUtils.addInfoMessage("Error : " + ex.getMessage());
                        log.log(Level.SEVERE, "Error", ex);
                        throw ex;
                    }
                    return;
                }
            }
        }
        onAction(actionId, null);
        fireEventSearchClosed();
    }

    public List<EditorActionConfig> getActionsGoto() {
        return getActionsByType("goto");
    }

    public List<EditorActionConfig> getActionsDialogOrInvoke() {
        return getActionsByType("dialog", "invoke");
    }

    public boolean isActionDialog(String actionId) {
        if (actionId != null) {
            ActionDialogAdapter ed = VrApp.getBean(ActionDialogManager.class).findAction(actionId);
            return ed.isDialog() || ed.isInvoke();
        }
        return false;
    }

    public void openActionDialog(String actionId) {
        if (actionId != null) {
            ActionDialogAdapter ed = VrApp.getBean(ActionDialogManager.class).findAction(actionId);
            if (ed != null) {
                if (ed.isConfirm()) {

                    getModel().setActionId(actionId);
                    getModel().setConfirmMessage("Etes vous sur de vouloir continuer?");
                    getModel().setOperationMessage(ed.getActionMessage());
                    getModel().setParams(ed.getParams());

                    new DialogBuilder("/modules/editor/confirm-dialog")
                            .setResizable(true)
                            .setDraggable(true)
                            .setModal(true)
                            .setHeight(120 + Math.min(5, getModel().getParams().length) * 40)
                            .setContentHeight("100%")
                            .open();
                } else {
                    getModel().setActionId(actionId);
                    proceedOpenActionDialog();
                }
            }
        }
    }

    public void onActionDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        if (o != null) {
            String value = o.getValue();
            Object[] args = null;
            if (value != null) {
                if (value.startsWith("[")) {
                    args = VrUtils.parseJSONObject(value, String[].class);
                } else {
                    args = new String[]{value};
                }
            }
            onAction(o.getUserInfo(), args);
        }
    }

    public void openPropertyViewDialog(String property, String action) {
        PropertyView p = findPropertyView(property);
        if (p != null) {
            PropertyViewDialog ed = VrApp.getBean(PropertyViewDialogManager.class).getPropertyViewDialog(p.getCtrlType(), action);
            if (ed != null) {
                ed.openDialog(p, action, p.getComponentId());
            }
        }
    }

    public void onSearchDialogClosed(SelectEvent event) {
//        System.out.println("onSearchDialogClosed");
//        onRefresh();
    }

    public void onPropertyViewDialogClosed(SelectEvent event) {
        DialogResult o = (DialogResult) event.getObject();
        if (o != null) {
            PropertyView p = findPropertyView(o.getUserInfo());
            if (p != null) {
                p.setValue(o.getValue());
            }
        }
    }

    public void switchWikiTextArea(PropertyView p) {
        String componentState = p.getComponentState();
        if ("Default".equals(componentState)) {
            p.setComponentState("Edit");
        } else if ("Edit".equals(componentState)) {
            p.setComponentState("Default");
        } else {
            p.setComponentState("Default");
        }
    }

    private void clearBulkUpdateSelections() {
        for (PropertyView property : properties) {
            property.setBulkSelected(false);
        }
    }

    private boolean hasBulkUpdates() {
        for (PropertyView property : properties) {
            if (property.isBulkSelected()) {
                return true;
            }
        }
        return false;
    }

    private void mergeBulkUpdates(Document target) {
        for (PropertyView property : properties) {
            if (property.isBulkSelected()) {
                property.storeTo(target);
            }
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

    public class PActionParam {

        private ActionParam param;
        private String svalue;
        private int index;

        public PActionParam(int index, ActionParam param) {
            this.param = param;
            this.index = index;
            Object v = this.param.getInitialValue();
            svalue = v == null ? "" : v.toString();
        }

        public String getName() {
            return param.getName();
        }

        public ParamType getType() {
            return param.getType();
        }

        public String getSvalue() {
            return svalue;
        }

        public void setSvalue(String svalues) {
            this.svalue = svalues;
        }

        public int getIndex() {
            return index;
        }
    }

    public class PModel extends Model<EditorRow> {

        private String confirmMessage;
        private String actionId;
        private String entityName;
        private String searchHelper = "Tapez ici les mots clés de recherche.";
        private String operationMessage;
        private List<EditorActionConfig> actions = new ArrayList<EditorActionConfig>();
        private List<VrEditorSearch> searchs = new ArrayList<>();
        private EditorConfig config;
        private Set<String> disabledFields = new HashSet<String>();
        private String searchText;
        private String searchType;
        private PActionParam[] params = new PActionParam[0];

        public PModel() {
            setCurrent(null);
        }

        public List<VrEditorSearch> getSearchs() {
            return searchs;
        }

        public void setSearchs(List<VrEditorSearch> searchs) {
            this.searchs = searchs;
        }

        public String getSearchType() {
            return searchType;
        }

        public void setSearchType(String searchType) {
            this.searchType = searchType;
            VrEditorSearch y = core.getEditorSearch(getModel().getSearchType());
            setSearchHelper(y.createHelperString(null, entityName));
        }

        public String getSearchHelper() {
            return searchHelper;
        }

        public void setSearchHelper(String searchHelper) {
            this.searchHelper = searchHelper;
        }

        public void setParams(ActionParam[] p) {
            if (p == null) {
                p = new ActionParam[0];
            }
            params = new PActionParam[p.length];
            for (int i = 0; i < params.length; i++) {
                params[i] = new PActionParam(i, p[i]);
            }
        }

        public PActionParam[] getParams() {
            return params;
        }

        public String getOperationMessage() {
            return operationMessage;
        }

        public void setOperationMessage(String operationMessage) {
            this.operationMessage = operationMessage;
        }

        public List<EditorActionConfig> getActions() {
            return actions;
        }

        public String getActionId() {
            return actionId;
        }

        public void setActionId(String actionId) {
            this.actionId = actionId;
        }

        public void setActions(List<EditorActionConfig> actions) {
            this.actions = actions;
        }

        public String getEntityName() {
            return entityName;
        }

        public String getEntityTitle() {
            return UPA.getPersistenceUnit().getEntity(entityName).getTitle();
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public List getSelectedObjects() {
            switch (getMode()) {
                case UPDATE:
                case PERSIST: {
                    return Arrays.asList(getCurrentDocument());
                }
                case BULK_UPDATE:
                case READ: {
                    List all = new ArrayList();
                    for (EditorRow objRow : getSelectedRows()) {
                        all.add(objRow.getDocument());
                    }
                    return all;
                }
            }
            return Collections.emptyList();
        }

        public Document getCurrentDocument() {
            EditorRow c = getCurrent();
            if (c == null) {
                return null;
            }
            return c.getDocument();
        }

        public EditorRow getCurrent() {
            return super.getCurrent();
        }

        @Override
        public void setCurrent(EditorRow current) {
            super.setCurrent(current); //To change body of generated methods, choose Tools | Templates.
        }

        public List<EditorRow> getList() {
            return super.getList();
        }

        public EditorConfig getConfig() {
            return config;
        }

        public void setConfig(EditorConfig config) {
            this.config = config;
        }

        public Set<String> getDisabledFields() {
            return disabledFields;
        }

        public String getConfirmMessage() {
            return confirmMessage;
        }

        public void setConfirmMessage(String confirmMessage) {
            this.confirmMessage = confirmMessage;
        }

        public void setDisabledFields(Set<String> disabledFields) {
            this.disabledFields = disabledFields;
        }

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

    }

    private static Function hashCssColor = new Function() {
        @Override
        public Object eval(FunctionEvalContext evalContext) {
            Object[] a = evalContext.getArguments();
            if (a.length == 0) {
                return "";
            }
            if (a[0] == null) {
                return "";
            }
            int h = a[0].getClass().isEnum() ? ((Enum) a[0]).ordinal() : a[0].hashCode();
            int r = Math.abs(h) % (CSS_COLOR_ARR.length);
            return CSS_COLOR_ARR[r];
        }
    };

    private static Function hashToStringArr = new Function() {
        @Override
        public Object eval(FunctionEvalContext evalContext) {
            Object[] a = evalContext.getArguments();
            if (a.length == 0) {
                return "";
            }
            if (a.length == 1) {
                return a[0] == null ? "" : String.valueOf(a[0]);
            }
            Object ss = a[0] == null ? "" : (a[0]);
            int h = ss.getClass().isEnum() ? ((Enum) ss).ordinal() : ss.hashCode();
            int x = 1 + (Math.abs(h) % (a.length - 1));
            return a[x] == null ? "" : String.valueOf(a[x]);
        }
    };
    Function inthash = new Function() {
        @Override
        public Object eval(FunctionEvalContext evalContext) {
            Object[] a = evalContext.getArguments();
            if (a.length == 0) {
                return 0;
            }
            if (a.length == 1) {
                if (a[0] == null) {
                    return 0;
                }
                return a[0].hashCode();
            }
            if (a.length == 2) {
                if (a[0] == null) {
                    return 0;
                }
                int h = a[0].hashCode();
                if (a[1] != null) {
                    int g = Math.abs(a[1].hashCode());
                    return h % g;
                }
                return h;
            }
            return a;
        }
    };

}
