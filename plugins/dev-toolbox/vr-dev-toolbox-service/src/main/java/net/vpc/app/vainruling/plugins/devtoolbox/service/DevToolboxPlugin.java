/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.devtoolbox.service;

import net.vpc.app.vainruling.core.service.AppPlugin;
import net.vpc.app.vainruling.core.service.Install;
import net.vpc.app.vainruling.core.service.UpaAware;


/**
 * @author vpc
 */
@AppPlugin(version = "1.0")
@UpaAware
public class DevToolboxPlugin {

    @Install
    public void installService() {
    }

}
