/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.articles.service;

import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.plugins.articles.service.model.ArticlesItem;
import net.vpc.upa.Entity;
import net.vpc.upa.Field;
import net.vpc.upa.callbacks.FieldEvent;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnCreate;
import net.vpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class ArticlesCallbacks {

    @OnCreate
    public void onCreateField(FieldEvent event) throws UPAException {
        Entity e = event.getEntity();
        Field f = event.getField();
        if (e.getEntityType().equals(ArticlesItem.class)) {
            if (f.getName().equals("sender")) {
                f.setDefaultObject(VrUtils.DEFAULT_OBJECT_CURRENT_USER);
            }
            if (f.getName().equals("sendTime")) {
                f.setDefaultObject(VrUtils.DEFAULT_OBJECT_CURRENT_DATETIME);
            }
        }
    }

}
