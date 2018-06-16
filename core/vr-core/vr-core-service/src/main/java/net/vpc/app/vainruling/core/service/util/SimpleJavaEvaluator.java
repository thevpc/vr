package net.vpc.app.vainruling.core.service.util;

import java.util.Set;
import net.vpc.common.jeep.DefaultExpressionEvaluator;
import net.vpc.common.jeep.ExpressionEvaluatorDefinition;
import net.vpc.common.jeep.ExpressionEvaluatorFactory;
import net.vpc.common.jeep.ExpressionEvaluatorResolver;
import net.vpc.common.jeep.ExpressionVariable;
import net.vpc.common.jeep.PlatformHelper;
import net.vpc.common.jeep.UtilClassExpressionEvaluatorResolver;

/**
 * Created by vpc on 4/16/17.
 */
public class SimpleJavaEvaluator implements InSetEvaluator {

    public static class ExtraHelper {

        public static boolean add(boolean a, boolean b) {
            return a && b;
        }

        public static boolean minus(boolean a, boolean b) {
            return a && !b;
        }
    }

    private static final ExpressionEvaluatorDefinition definition = new ExpressionEvaluatorDefinition();

    {
        definition.declareBinaryOperators("+", "-", "&", "|", "&&", "||");
        definition.declareOperatorAlias("et", "&&", new Class[]{Boolean.TYPE, Boolean.TYPE});
        definition.declareOperatorAlias("and", "&&", new Class[]{Boolean.TYPE, Boolean.TYPE});
        definition.declareOperatorAlias("sauf", "-", new Class[]{Boolean.TYPE, Boolean.TYPE});
        definition.declareOperatorAlias("but", "-", new Class[]{Boolean.TYPE, Boolean.TYPE});
        definition.declareConst("true", Boolean.TRUE);
        definition.declareConst("all", Boolean.TRUE);
        definition.declareConst("tous", Boolean.TRUE);
        definition.declareConst("false",  Boolean.FALSE);
        definition.declareConst("aucun",  Boolean.FALSE);
        definition.declareConst("none",  Boolean.FALSE);
        definition.addResolver(new UtilClassExpressionEvaluatorResolver(PlatformHelper.class, ExtraHelper.class));
        definition.addResolver(new ExpressionEvaluatorResolver() {
            @Override
            public ExpressionVariable resolveVar(String name, ExpressionEvaluatorDefinition definition) {
                return ExpressionEvaluatorFactory.createConstVar(name, items.contains(name.toLowerCase()));
            }

        });
    }

    private final DefaultExpressionEvaluator evaluator = new DefaultExpressionEvaluator();

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
