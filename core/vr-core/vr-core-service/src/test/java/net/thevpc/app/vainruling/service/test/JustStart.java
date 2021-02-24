package net.thevpc.app.vainruling.service.test;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.common.util.Chronometer;
import org.junit.Test;

public class JustStart {

    @Test
    public void runStandaone() {
        Chronometer ch = Chronometer.start();
        VrApp.runStandalone();
        ch.stop();
        System.out.println("started in " + ch);
        org.junit.Assert.assertTrue(CorePlugin.get().findNavigatablePeriods().size() > 0);
    }
}
