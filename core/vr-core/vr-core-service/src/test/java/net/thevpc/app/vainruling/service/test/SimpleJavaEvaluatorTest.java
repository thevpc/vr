/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.service.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.thevpc.app.vainruling.core.service.util.SimpleJavaEvaluator;
import net.thevpc.common.time.Chronometer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author vpc
 */
public class SimpleJavaEvaluatorTest {

    @Test
    public void test1() {
        SimpleJavaEvaluator e = new SimpleJavaEvaluator();
        Set<String> all = new HashSet<String>(Arrays.asList("IA", "GTE"));
//       Assert.assertEquals(false, e.evaluateExpression("a b", all));
//       Assert.assertEquals(true, e.evaluateExpression("ia", all));
//       Assert.assertEquals(true, e.evaluateExpression("ia rt", all));
//       Assert.assertEquals(true, e.evaluateExpression("(ia+rt) gte", all));
        Assert.assertEquals(true, e.evaluateExpression("ia ou rt", all));
    }

    @Test
    public void testPerf() {
        int max = 10000;
        boolean t = false;
        Chronometer c = Chronometer.start();
        for (int i = 0; i < max; i++) {
            t = SimpleJavaEvaluator.INSTANCE.evaluateExpression("( jamel.belhadjtaher,aref.meddeb,taha.bensalah,IA,MasterGT,MsGT ) , taha.bensalah", new HashSet<>(Arrays.asList("theboss", "titi", "MsGT")));
        }
        c.stop();
        System.out.println("eval :" + c + " ==> " + t + (" =" + c.getTime()));
        Assert.assertTrue(t);
        Assert.assertTrue(c.getTime() < 1000_000_000);
    }

}
