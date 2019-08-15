/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.service.extensions.data;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.upa.CustomDefaultObject;
import net.vpc.upa.events.FieldEvent;
import net.vpc.upa.config.*;


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
