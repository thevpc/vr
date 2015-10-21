/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import net.vpc.app.vainruling.api.CorePlugin;
import net.vpc.app.vainruling.api.VrApp;

/**
 *
 * @author vpc
 */
public class VrAppTest {

    public static void runStandalone() {
        VrApp.runStandalone(CorePlugin.USER_ADMIN, "admin", true, new String[0]);
    }

    public static void runStandalone(String login, String password) {
        VrApp.runStandalone(login, password, true, new String[0]);
    }
}
