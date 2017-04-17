package net.vpc.app.vainruling.core.service.util;

import net.vpc.common.strings.*;

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

        evaluator.declareBinaryOperator(",", 1, "|", "ou", "or", " ");
        evaluator.declareConst("true", true, "all", "tous");
        evaluator.declareConst("false", false, "none", "aucun");
        this.items = set;
    }

    @Override
    public <T> T evaluateExpression(String expression) {
        return evaluator.evaluate(expression, new AbstractExpressionEvaluator() {
            @Override
            public Object evalVar(ExprContext context, ExprVar var) {
                return items.contains(var.getName().toLowerCase());
            }

            @Override
            public Object evalOperator(ExprContext context, ExprOperator op) {
                switch (op.getName()) {
                    case "+": {
                        Boolean o1 = (Boolean) op.getOperand(0).eval(context, this);
                        if (!o1) {
                            return false;
                        }
                        return (Boolean) op.getOperand(1).eval(context, this);
                    }
                    case "-": {
                        Boolean o1 = (Boolean) op.getOperand(0).eval(context, this);
                        if (!o1) {
                            return false;
                        }
                        Boolean o2 = (Boolean) op.getOperand(1).eval(context, this);
                        return (!o2);
                    }
                    case ",": {
                        Boolean o1 = (Boolean) op.getOperand(0).eval(context, this);
                        if (o1) {
                            return true;
                        }
                        return ((Boolean) op.getOperand(1).eval(context, this));
                    }
                }
                return false;
            }

            @Override
            public Object evalFunction(ExprContext context, ExprFunction fct) {
                return false;
            }
        });
    }


}
