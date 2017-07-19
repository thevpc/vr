package net.vpc.app.vr.plugins.academicprofile.service;

import net.vpc.app.vainruling.core.service.plugins.AppPlugin;
import net.vpc.app.vainruling.core.service.plugins.Install;
import net.vpc.app.vainruling.core.service.plugins.Start;

/**
 * Created by vpc on 7/19/17.
 */
@AppPlugin
public class AcademicProfilePlugin {

    @Install
    public void installService() {
        //called if the plugin is installed : first install of new version
    }

    @Start
    public void startService() {
        //called at the startup of the server
    }

    public void customMethod(){

    }
}
