/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.extensions.data;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.upa.CustomDefaultObject;
import net.thevpc.upa.events.FieldEvent;
import net.thevpc.upa.config.*;


/**
 * @author taha.bensalah@gmail.com
 */
@Callback
public class AppArticleDaoExtension {

    @OnPreCreate
    public void onPreCreate(FieldEvent event) {
        if (event.getField().getAbsoluteName().equals("AppArticle.disposition")) {
            event.getField().setDefaultObject((CustomDefaultObject) () -> CorePlugin.get().findArticleDisposition("Welcome"));
        }
    }

}
