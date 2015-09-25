/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.commonmodel.service;

import net.vpc.app.vainruling.api.AppPlugin;
import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.Start;
import net.vpc.app.vainruling.api.model.AppPeriod;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author vpc
 */
@AppPlugin(version = "1.0")
public class CommonModelPlugin {

    @Autowired
    CorePlugin core;

    @Start
    public void start() {
    }

    public AppPeriod findPeriod(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        return (AppPeriod) pu.createQuery("Select u from AppPeriod u where (u.snapshotName=null or u.snapshotName='') and u.name=:name")
                .setParameter("name", name).getEntity();
    }

}
