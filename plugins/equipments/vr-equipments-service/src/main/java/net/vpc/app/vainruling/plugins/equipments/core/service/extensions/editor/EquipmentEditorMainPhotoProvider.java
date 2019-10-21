/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.editor;

import net.vpc.app.vainruling.core.service.editor.PropertyMainPhotoProvider;
import org.springframework.stereotype.Component;
import net.vpc.app.vainruling.VrEntityName;

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
