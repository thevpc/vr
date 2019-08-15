/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.plugins.tasks.service.model.TodoPriority;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatusType;
import net.vpc.common.jsf.FacesUtils;
import org.springframework.context.annotation.Scope;

import javax.faces.model.SelectItem;
import net.vpc.app.vainruling.core.service.pages.VrPage;

/**
 * @author taha.bensalah@gmail.com
 */
@VrPage
@Scope(value = "singleton")
public class DropDownCtrl {

    public SelectItem[] getTodoPriorities() {
        SelectItem[] items = new SelectItem[TodoPriority.values().length];
        int i = 0;
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoPriority.VERY_LOW), "tres basse");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoPriority.LOW), "basse");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoPriority.DEFAULT), "normale");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoPriority.HIGH), "haute");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoPriority.VERY_HIGH), "tres haute");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoPriority.EXTREMELY_URGENT), "immediate");
        return items;
    }

    public SelectItem[] getTodoStatusTypes() {
        SelectItem[] items = new SelectItem[TodoStatusType.values().length];
        int i = 0;
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoStatusType.UNASSIGNED), "à assigner");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoStatusType.ASSIGNED), "en cours");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoStatusType.TO_VERIFY), "à vérifier");
        items[i++] = FacesUtils.createSelectItem(String.valueOf(TodoStatusType.DONE), "terminé");
        return items;
    }
}
