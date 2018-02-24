package net.vpc.app.vainruling.service.test;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.common.util.Chronometer;

public class JustStart {
    public static void main(String[] args) {
        Chronometer ch = new Chronometer();
        VrApp.runStandalone();
    }
}
