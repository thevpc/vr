package net.vpc.app.vr.plugins.academicprofile.web;

import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vr.plugins.academicprofile.service.AcademicProfilePlugin;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by vpc on 7/19/17.
 */
/*
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Mes Stages",
        menu = "/Desktop",
        url = "modules/academic/profile/my-profile"
)
*/
public class MyCtrl {
    @Autowired
    private  AcademicProfilePlugin profile;

    @OnPageLoad
    private void onPageReload(){
        //called each time the page is invoked from an other page
    }

    void doIt(){
        //profile.
        //any action
    }
}
