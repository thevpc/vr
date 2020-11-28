/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.editor;

import net.thevpc.app.vainruling.VrEditorAction;
import net.thevpc.app.vainruling.VrEditorActionProcessor;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.PlatformReflector;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
public class ActionDialogManager {


    private Map<String, ActionDialogAdapter> byActionName;
    private Map<String, List<ActionDialogAdapter>> byEntityName = new HashMap<>();

    public ActionDialogAdapter findAction(String name) {
        return getByActionName().get(name);
    }

    public synchronized List<ActionDialogAdapter> findActionsByEntity(String entityName) {

        List<ActionDialogAdapter> list = byEntityName.get(entityName);
        if (list == null) {
            list = new ArrayList<>();
            for (ActionDialogAdapter value : getByActionName().values()) {
                if (value.acceptEntity(entityName)) {
                    list.add(value);
                }
            }
            byEntityName.put(entityName, list);
        }
        return list;
    }

    public synchronized Map<String, ActionDialogAdapter> getByActionName() {
        if (byActionName == null) {
            byActionName = new HashMap<>();
            for (Object o : VrApp.getBeansMapForAnnotations(VrEditorAction.class).values()) {
                if (o instanceof VrEditorActionProcessor) {
                    VrEditorActionProcessor a = (VrEditorActionProcessor) o;
                    ActionDialogAdapter aa = new ActionDialogAdapter(a);
                    if (byActionName.containsKey(aa.getId())) {
                        throw new IllegalArgumentException("Ambiguous name " + aa.getId());
                    }
                    byActionName.put(aa.getId(), aa);
                } else {
                    throw new IllegalArgumentException(PlatformReflector.getTargetClass(o) + " must implement " + VrEditorActionProcessor.class);
                }
            }
        }
        return byActionName;
    }


}
