/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.service.test;

import java.util.Arrays;
import java.util.HashSet;
import net.thevpc.app.vainruling.core.service.util.SimpleJavaEvaluator;
import net.thevpc.common.util.Chronometer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author vpc
 */
public class TestEval {
    //    public static void main(String[] args) {
//        HashSet<String> set = new HashSet<>();
//        SimpleJavaEvaluator s = new SimpleJavaEvaluator(set);
////        Object o = s.evaluateExpression("IA('EI' MC)");
////        Object o = s.evaluateExpression("(IA,MC)+Student,(IA,(MC))+Student");
//        Object o = s.evaluator.parse("(IA,MC)+Student,\'Student\'('IA',(\"MC\"))");
////        Object o = s.evaluateExpression("IA,MC+Student");
//        System.out.println(o);
//    }

//    @Test
//    public void testSimpleJavaEvaluator() {
//        SimpleJavaEvaluator s = new SimpleJavaEvaluator(new HashSet<>(Arrays.asList("theboss", "titi", "Ei2")));
//        Assert.assertTrue(s.evaluateExpression("EI2 (EI2+Student),abdelaziz.theboss"));
//        Assert.assertFalse(s.evaluateExpression("(EI2+Student),abdelaziz.theboss"));
//        Assert.assertFalse(s.evaluateExpression("),abdelaziz.theboss"));
//    }
    public static void main(String[] args) {
        Chronometer c0 = Chronometer.start();
        int max=10000;
        for (int i = 0; i < max; i++) {
            System.out.println("============================ "+(i+1)+"/"+max);
            Chronometer c = Chronometer.start();
              System.out.println("new  :"+c);
            boolean t = SimpleJavaEvaluator.INSTANCE.evaluateExpression("( jamel.belhadjtaher,aref.meddeb,taha.bensalah,IA,MasterGT,MsGT ) , taha.bensalah", new HashSet<>(Arrays.asList("theboss", "titi", "MsGT")));
            c.stop();
            System.out.println("eval :"+c+" ==> "+t);
        }
        c0.stop();
        System.out.println(c0);
    }

    @Test
    public void testSimpleJavaEvaluator2() {
        Chronometer c = Chronometer.start();
        boolean t = SimpleJavaEvaluator.INSTANCE.evaluateExpression("( jamel.belhadjtaher,aref.meddeb,taha.bensalah,IA,MasterGT,MsGT ) , taha.bensalah", new HashSet<>(Arrays.asList("theboss", "titi", "MsGT")));
        c.stop();
        System.out.println(c);
        Assert.assertTrue(t);
    }

}
