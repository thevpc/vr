/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import net.vpc.app.vainruling.core.service.TraceService;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.common.util.Chronometer;
import net.vpc.upa.*;
import net.vpc.upa.bulk.ImportPersistenceUnitListener;
import net.vpc.upa.exceptions.UPAException;
import net.vpc.upa.filters.EntityFilter;

/**
 * @author taha.bensalah@gmail.com
 */
public class BiPU {

    public static void main(String[] args) {
        BiPU b = new BiPU();
        b.go();
    }

    public void go() {
        VrApp.runStandalone("admin", "vilain77");
//        net.vpc.common.util.LogUtils.configure(Level.SEVERE, "net.vpc");
        final PersistenceUnit source = UPA.getPersistenceUnit("main");
        final PersistenceUnit target = UPA.getPersistenceUnit("mysql");
        target.beginStructureModification();
        for (Entity entity : target.getEntities()) {
            entity.setUserModifiers(entity.getUserModifiers().add(EntityModifier.CLEAR));
        }
        target.commitStructureModification();
        EntityFilter filterOne = new EntityFilter() {
            @Override
            public boolean accept(Entity entity) throws UPAException {
                return entity.getName().equals("AppUser");
            }
        };

        final EntityFilter filter = null;//filterOne
        final boolean clear = false;

        Chronometer chronometer = new Chronometer();
        source.invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                target.invokePrivileged(new VoidAction() {
                    @Override
                    public void run() {
                        //target.updateFormulas(AppUser.class,null,null);
                        VrApp.getBean(TraceService.class).archiveLogs(1);
                        if (clear) {
                            target.clear(filter, null);
                        }
                        target.getImportExportManager().importEntities(source, filter, clear, new ImportPersistenceUnitListener() {
                            @Override
                            public void objectPersisted(String entityName, Object source, Object target) {

                            }

                            @Override
                            public void objectMerged(String entityName, Object source, Object target) {

                            }

                            @Override
                            public void objectPersistFailed(String entityName, Object source, Object target, Exception error) throws Exception {
                                throw error;
                            }

                            @Override
                            public void objectMergeFailed(String entityName, Object source, Object target, Exception error) throws Exception {
                                throw error;
                            }
                        });
                    }
                });
            }
        });
        System.out.println("Import finished in " + chronometer.stop());
    }


}
