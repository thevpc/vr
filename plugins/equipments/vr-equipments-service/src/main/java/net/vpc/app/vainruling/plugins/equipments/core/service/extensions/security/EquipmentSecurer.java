/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.equipments.core.service.extensions.security;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppDepartment;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.plugins.equipments.core.service.EquipmentPluginHelper;
import net.vpc.app.vainruling.plugins.equipments.core.model.Equipment;
import net.vpc.upa.*;
import net.vpc.upa.config.SecurityContext;

/**
 * @author taha.bensalah@gmail.com
 */
@SecurityContext(entity = "Equipment")
public class EquipmentSecurer extends DefaultEntitySecurityManager {

    public EquipmentSecurer() {
    }

    @Override
    public boolean isAllowedUpdate(Entity entity, Object id, Object value) {
        boolean allowed = super.isAllowedUpdate(entity, id, value);
        if (!allowed) {
            return false;
        }
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        UserToken us = core.getCurrentToken();
        if (us != null) {
            if(us.isAdmin()){
                return true;
            }
            AppDepartment d = core.getCurrentUser().getDepartment();
            if (d != null) {
                if (value instanceof Document) {
                    value = entity.getBuilder().documentToObject((Document) value);
                }
                Equipment e = (Equipment) value;
                AppDepartment d2 = e.getDepartment();
                if (d2 != null) {
                    if (d.getId() == d2.getId()) {
                        return true;
                    }
                    EquipmentPluginHelper.ensureCreatedEquipmentDepartmentUpdateRight(d);
                    return core.isCurrentAllowed(EquipmentPluginHelper.getEquipmentDepartmentUpdateRight(d));
                }

            }
        }
        return false;
    }


    //    @Override
//    public boolean getAllowedReadPermission(Field field) throws UPAException {
//        if(field.getAbsoluteName().equals("AcademicTeacherCV.viewsCounter")){
//           return VrApp.getBean(CorePlugin.class).isUserSessionAdmin();
//        }
//        return super.getAllowedReadPermission(field);
//    }
//    
//    @Override
//    public boolean getAllowedWritePermission(Field field) throws UPAException {
//        if(field.getAbsoluteName().equals("AcademicTeacherCV.viewsCounter")){
//           return VrApp.getBean(CorePlugin.class).isUserSessionAdmin();
//        }
//        return super.getAllowedReadPermission(field);
//    }


}
