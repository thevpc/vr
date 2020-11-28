/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.service.extensions.editor;

import net.thevpc.app.vainruling.VrEntityName;
import net.thevpc.app.vainruling.core.service.editor.PropertyMainPhotoProvider;
import org.springframework.stereotype.Component;

/**
 *
 * @author vpc
 */
@Component
@VrEntityName("Equipment")
public class EquipmentEditorMainPhotoProvider extends PropertyMainPhotoProvider {

    public EquipmentEditorMainPhotoProvider() {
        super("photo", "private-theme-context://images/equipment.png");
    }

}
