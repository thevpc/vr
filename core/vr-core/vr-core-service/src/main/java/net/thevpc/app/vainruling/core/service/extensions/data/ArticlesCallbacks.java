/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.data;

import net.thevpc.app.vainruling.core.service.model.content.AppArticle;
import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.upa.Entity;
import net.thevpc.upa.Field;
import net.thevpc.upa.events.FieldEvent;
import net.thevpc.upa.config.Callback;
import net.thevpc.upa.config.OnCreate;
import net.thevpc.upa.exceptions.UPAException;

/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class ArticlesCallbacks {
    @OnCreate
    public void onCreateField(FieldEvent event) throws UPAException {
        Entity e = event.getEntity();
        Field f = event.getField();
        if (e.getEntityType().equals(AppArticle.class)) {
            if (f.getName().equals("sender")) {
                f.setDefaultObject(VrUtils.DEFAULT_OBJECT_CURRENT_USER);
            }
            if (f.getName().equals("sendTime")) {
                f.setDefaultObject(VrUtils.DEFAULT_OBJECT_CURRENT_DATETIME);
            }
        }
    }

}
