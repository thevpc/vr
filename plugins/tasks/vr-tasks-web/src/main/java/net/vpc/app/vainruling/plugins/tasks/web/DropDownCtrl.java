/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoPriority;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatusType;
import org.springframework.context.annotation.Scope;

import javax.faces.model.SelectItem;

/**
 * @author taha.bensalah@gmail.com
 */
@VrController
@Scope(value = "singleton")
public class DropDownCtrl {

    public SelectItem[] getTodoPriorities() {
        SelectItem[] items = new SelectItem[TodoPriority.values().length];
        int i = 0;
        items[i++] = new SelectItem(TodoPriority.VERY_LOW, "tres basse");
        items[i++] = new SelectItem(TodoPriority.LOW, "basse");
        items[i++] = new SelectItem(TodoPriority.DEFAULT, "normale");
        items[i++] = new SelectItem(TodoPriority.HIGH, "haute");
        items[i++] = new SelectItem(TodoPriority.VERY_HIGH, "tres haute");
        items[i++] = new SelectItem(TodoPriority.EXTREMELY_URGENT, "immediate");
        return items;
    }

    public SelectItem[] getTodoStatusTypes() {
        SelectItem[] items = new SelectItem[TodoStatusType.values().length];
        int i = 0;
        items[i++] = new SelectItem(TodoStatusType.UNASSIGNED, "à assigner");
        items[i++] = new SelectItem(TodoStatusType.ASSIGNED, "en cours");
        items[i++] = new SelectItem(TodoStatusType.TO_VERIFY, "à vérifier");
        items[i++] = new SelectItem(TodoStatusType.DONE, "terminé");
        return items;
    }
}
