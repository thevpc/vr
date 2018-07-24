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

    private final ExpressionEvaluatorDefinition definition ;


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
        definition= new ExpressionEvaluatorDefinition();
        definition.declareBinaryOperators("+", "-", "&&", "||");

        definition.declareOperatorAlias("et", "&&", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("and", "&&", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("&", "&&", Boolean.TYPE, Boolean.TYPE);

        definition.declareOperatorAlias(",", "||", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("ou", "||", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("or", "||", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("|" , "||", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("", "or", Boolean.TYPE, Boolean.TYPE);

        definition.declareOperatorAlias("sauf", "-", Boolean.TYPE, Boolean.TYPE);
        definition.declareOperatorAlias("but", "-", Boolean.TYPE, Boolean.TYPE);


        definition.declareConst("true", true);
        definition.declareConst("all", true);
        definition.declareConst("tous", true);
        definition.declareConst("false", false);
        definition.declareConst("aucun", false);
        definition.declareConst("none", false);

        definition.importType(PlatformHelper.class);
        definition.importType(ExtraHelper.class);
        definition.addResolver(new ExpressionEvaluatorResolver() {
            @Override
            public ExpressionVariable resolveVarDef(String name, ExpressionEvaluatorDefinition definition) {
                return ExpressionEvaluatorFactory.createConstVar(name, items.contains(name.toLowerCase()));
            }

        });
        evaluator=new DefaultExpressionEvaluator(definition);
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
