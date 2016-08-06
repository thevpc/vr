/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inventory.service;

import net.vpc.app.vainruling.core.service.AppPlugin;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.Install;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author taha.bensalah@gmail.com
 */
@AppPlugin(
        version = "1.0", dependsOn = {"equipmentPlugin"}
)
public class InventoryPlugin {

    @Autowired
    CorePlugin core;

    @Install
    public void installService() {
//        AppProfile technicianProfile;
//        technicianProfile = new AppProfile();
//        technicianProfile.setName("Technician");
//        technicianProfile = core.insertOrUpdate(technicianProfile);
//
//        AppProfile headOfDepartment;
//        headOfDepartment = new AppProfile();
//        headOfDepartment.setName(CorePlugin.HEAD_OF_DEPARTMENT);
//        headOfDepartment = core.insertOrUpdate(headOfDepartment);
//
//        for (net.vpc.upa.Entity ee : UPA.getPersistenceUnit().getPackage("Equipment/Inventory").getEntities(true)) {
//            core.profileAddRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Persist");
//            core.profileAddRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Remove");
//            core.profileAddRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Update");
//            core.profileAddRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Navigate");
//            core.profileAddRight(headOfDepartment.getId(), ee.getAbsoluteName() + ".Load");
//
//            core.profileAddRight(technicianProfile.getId(), ee.getAbsoluteName() + ".Persist");
//            core.profileAddRight(technicianProfile.getId(), ee.getAbsoluteName() + ".Update");
//            core.profileAddRight(technicianProfile.getId(), ee.getAbsoluteName() + ".Navigate");
//            core.profileAddRight(technicianProfile.getId(), ee.getAbsoluteName() + ".Load");
//        }
    }

    @Install
    public void installSemoService() {
//        Inventory v = new Inventory();
//        v.setName("INV-2015");
//        v = core.insertOrUpdate(v);
    }
}
