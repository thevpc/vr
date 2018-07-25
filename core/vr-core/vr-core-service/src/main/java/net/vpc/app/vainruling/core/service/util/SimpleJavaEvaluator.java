package net.vpc.app.vainruling.core.service.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.vpc.common.jeep.*;

/**
 * Created by vpc on 4/16/17.
 */
public class SimpleJavaEvaluator implements InSetEvaluator {

    public static void main(String[] args) {
        SimpleJavaEvaluator s = new SimpleJavaEvaluator(new HashSet<>(Arrays.asList("toto", "titi")));
        Object v = s.evaluateExpression("toti , toto");
        System.out.println(v);
    }

    public static class ExtraHelper {

        public static boolean add(boolean a, boolean b) {
            return a && b;
        }

        public static boolean minus(boolean a, boolean b) {
            return a && !b;
        }
    }

    private final DefaultExpressionEvaluator evaluator;

    private Set<String> items;

    public SimpleJavaEvaluator(Set<String> set) {
//        evaluator = new ExpressionParser();
//        evaluator.setCaseSensitive(false);
//        evaluator.declareBinaryOperator("-", 3, "sauf", "but");
//        evaluator.declareBinaryOperator("+", 3, "&", "et", "and");
//
//        evaluator.declareBinaryOperator(",", 1, "|", ";", "ou", "or", " ");
//        evaluator.declareConst("true", true, "all", "tous");
//        evaluator.declareConst("false", false, "none", "aucun");
//        evaluator.setParseFunctions(true);
        this.items = set;
        evaluator=new DefaultExpressionEvaluator();
        evaluator.declareBinaryOperators("+", "-", "&&", "||");

        evaluator.declareOperatorAlias("et", "&&", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("and", "&&", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("&", "&&", Boolean.TYPE, Boolean.TYPE);

        evaluator.declareOperatorAlias(",", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("ou", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("or", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("|" , "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("", "or", Boolean.TYPE, Boolean.TYPE);

        evaluator.declareOperatorAlias("sauf", "-", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("but", "-", Boolean.TYPE, Boolean.TYPE);


        evaluator.declareConst("true", true);
        evaluator.declareConst("all", true);
        evaluator.declareConst("tous", true);
        evaluator.declareConst("false", false);
        evaluator.declareConst("aucun", false);
        evaluator.declareConst("none", false);

        evaluator.importType(PlatformHelper.class);
        evaluator.importType(ExtraHelper.class);
        for (String item : items) {
            evaluator.declareVar(item,Boolean.TYPE,true);
        }
    }

    //    public static void main(String[] args) {
//        HashSet<String> set = new HashSet<>();
//        SimpleJavaEvaluator s = new SimpleJavaEvaluator(set);
////        Object o = s.evaluateExpression("IA('EI' MC)");
////        Object o = s.evaluateExpression("(IA,MC)+Student,(IA,(MC))+Student");
//        Object o = s.evaluator.parse("(IA,MC)+Student,\'Student\'('IA',(\"MC\"))");
////        Object o = s.evaluateExpression("IA,MC+Student");
//        System.out.println(o);
//    }
    @Override
    public <T> T evaluateExpression(String expression) {
        Object b = evaluator.evaluate(expression);
        if (b instanceof Boolean) {
            return (T) b;
        }
        return (T) (Object) (b != null);
    }
}
