/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;

/**
 * @author vpc
 */
public class VrAppTest {

    public static void runStandalone() {
        VrApp.runStandalone(CorePlugin.USER_ADMIN, "admin", true);
    }

    public static void runStandaloneNoLog() {
        VrApp.runStandalone(CorePlugin.USER_ADMIN, "admin", false);
    }

    public static void runStandalone(String login, String password) {
        VrApp.runStandalone(login, password, true);
    }
}
