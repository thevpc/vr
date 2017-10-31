package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.strings.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vpc on 4/16/17.
 */
public class SimpleJavaEvaluator implements InSetEvaluator {

    private final ExpressionParser evaluator;

    private Set<String> items;

    public SimpleJavaEvaluator(Set<String> set) {
        evaluator = new ExpressionParser();
        evaluator.setCaseSensitive(false);
        evaluator.declareBinaryOperator("-", 3, "sauf", "but");
        evaluator.declareBinaryOperator("+", 3, "&", "et", "and");

        evaluator.declareBinaryOperator(",", 1, "|", ";","ou", "or", " ");
        evaluator.declareConst("true", true, "all", "tous");
        evaluator.declareConst("false", false, "none", "aucun");
        evaluator.setParseFunctions(true);
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
        SimpleExpressionEvaluator evaluator = new SimpleExpressionEvaluator();
        return (T) (Object) evaluator.asBool(this.evaluator.evaluate(expression, evaluator));
    }


    private class SimpleExpressionEvaluator extends AbstractExpressionEvaluator {

        public boolean evalVarAsBool(String name) {
            return items.contains(name.toLowerCase());
        }

        @Override
        public Object evalVar(ExprContext context, ExprVar var) {
            return evalVarAsBool(var.getName());
        }

        @Override
        public Object evalStr(ExprContext context, ExprStr strVal) {
            return strVal.getValue();
//                return super.evalStr(context, strVal);
        }

        @Override
        public Object evalConst(ExprContext context, ExprConst constVal) {
            return super.evalConst(context, constVal);
        }

        @Override
        public Object evalVal(ExprContext context, ExprVal val) {
            return super.evalVal(context, val);
        }

        private boolean asBool(Object o) {
            if(o instanceof Boolean){
                return (boolean) o;
            }
            return evalVarAsBool(String.valueOf(o));
        }

        @Override
        public Object evalFunction(ExprContext context, ExprFunction fct) {
            return false;
        }

        @Override
        public Object evalOperator(ExprContext context, ExprOperator op) {
            switch (op.getName()) {
                case "+": {
                    boolean o1 = asBool(op.getOperand(0).eval(context, this));
                    if (!o1) {
                        return false;
                    }
                    return asBool(op.getOperand(1).eval(context, this));
                }
                case "-": {
                    boolean o1 = asBool(op.getOperand(0).eval(context, this));
                    if (!o1) {
                        return false;
                    }
                    boolean o2 = asBool(op.getOperand(1).eval(context, this));
                    return (!o2);
                }
                case ",": {
                    boolean o1 = asBool(op.getOperand(0).eval(context, this));
                    if (o1) {
                        return true;
                    }
                    return asBool(op.getOperand(1).eval(context, this));
                }
            }
            return false;
        }

    }
}
