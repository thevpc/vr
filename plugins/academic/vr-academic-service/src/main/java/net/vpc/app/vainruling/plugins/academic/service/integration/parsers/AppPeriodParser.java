/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.parsers;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.integration.NameMap;
import net.vpc.app.vainruling.core.service.model.AppPeriod;

/**
 *
 * @author vpc
 */
public class AppPeriodParser {

    private NameMap<Integer, AppPeriod> cache = null;

    public void invalidate() {
        cache = null;
    }

    public void prepare() {
        if (cache == null) {
            cache = new NameMap<>(false);
            for (AppPeriod a : CorePlugin.get().findPeriods()) {
                cache.put(a.getId(), a,
                        new String[]{
                            a.getName()
                        });/*a.getOtherNames()*/;
            }
        }
    }

    public AppPeriod get(String name) {
        prepare();
        return cache.getByName(name);
    }

}
