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
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author vpc
 */
public class SimpleJavaEvaluatorTest {
   
   @Test
   public void test1(){
       SimpleJavaEvaluator e=new SimpleJavaEvaluator();
       Set<String> all=new HashSet<String>(Arrays.asList("IA","GTE"));
//       Assert.assertEquals(false, e.evaluateExpression("a b", all));
//       Assert.assertEquals(true, e.evaluateExpression("ia", all));
//       Assert.assertEquals(true, e.evaluateExpression("ia rt", all));
//       Assert.assertEquals(true, e.evaluateExpression("(ia+rt) gte", all));
       Assert.assertEquals(true, e.evaluateExpression("ia ou rt", all));
   } 
}
