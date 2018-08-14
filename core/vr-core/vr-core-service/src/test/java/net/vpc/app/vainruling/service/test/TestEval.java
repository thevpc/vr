/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import java.util.Arrays;
import java.util.HashSet;
import net.vpc.app.vainruling.core.service.util.SimpleJavaEvaluator;
import net.vpc.common.util.Chronometer;
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
        int max=1000;
        for (int i = 0; i < max; i++) {
            System.out.println("============================ "+(i+1)+"/"+max);
            Chronometer c = new Chronometer();
            SimpleJavaEvaluator s = new SimpleJavaEvaluator(new HashSet<>(Arrays.asList("theboss", "titi", "MsGT")));
//            System.out.println("new  :"+c);
            boolean t = s.evaluateExpression("( jamel.belhadjtaher,aref.meddeb,taha.bensalah,IA,MasterGT,MsGT ) , taha.bensalah");
            c.stop();
            System.out.println("eval :"+c);
        }
    }

    @Test
    public void testSimpleJavaEvaluator2() {
        Chronometer c = new Chronometer();
        SimpleJavaEvaluator s = new SimpleJavaEvaluator(new HashSet<>(Arrays.asList("theboss", "titi", "MsGT")));
        boolean t = s.evaluateExpression("( jamel.belhadjtaher,aref.meddeb,taha.bensalah,IA,MasterGT,MsGT ) , taha.bensalah");
        c.stop();
        System.out.println(c);
        Assert.assertTrue(t);
    }

}
