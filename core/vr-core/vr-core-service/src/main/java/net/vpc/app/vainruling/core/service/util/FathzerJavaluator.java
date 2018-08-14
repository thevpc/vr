package net.vpc.app.vainruling.core.service.util;

import com.fathzer.soft.javaluator.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by vpc on 4/16/17.
 */
public class FathzerJavaluator extends AbstractEvaluator<Boolean> implements InSetEvaluator {

    /**
     * The logical AND operator.
     */
    final static Operator AND = new Operator("&", 2, Operator.Associativity.LEFT, 2);
    final static Operator AND2 = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
    final static Operator AND3 = new Operator("+", 2, Operator.Associativity.LEFT, 2);
    final static Operator EXCEPT = new Operator("-", 2, Operator.Associativity.LEFT, 2);
    /**
     * The logical OR operator.
     */
    final static Operator OR = new Operator("|", 2, Operator.Associativity.LEFT, 1);
    final static Operator OR2 = new Operator("||", 2, Operator.Associativity.LEFT, 1);
    final static Operator OR3 = new Operator(",", 2, Operator.Associativity.LEFT, 1);
    final static Operator OR4 = new Operator(";", 2, Operator.Associativity.LEFT, 1);

    private static final Parameters evalParams;

    static {
        // Create the evaluator's parameters
        evalParams = new Parameters();
        // Add the supported operators
        evalParams.add(AND);
        evalParams.add(AND2);
        evalParams.add(AND3);
        evalParams.add(OR);
        evalParams.add(OR2);
        evalParams.add(OR3);

        evalParams.add(EXCEPT);
        // Add the parentheses
        evalParams.addExpressionBracket(BracketPair.PARENTHESES);
        evalParams.addConstants(Arrays.asList(new Constant("all")));
        evalParams.addConstants(Arrays.asList(new Constant("none")));
        evalParams.setFunctionArgumentSeparator('\0');
    }

    private Set<String> items;

    public FathzerJavaluator(Set<String> set) {
        super(evalParams);
        this.items = set;
    }

    @Override
    protected Boolean toValue(String item, Object o) {
        if (item.equals("all")) {
            return true;
        }
        return items.contains(item.toLowerCase());
    }

    @Override
    protected Boolean evaluate(Constant constant, Object evaluationContext) {
        if (constant.getName().equals("all")) {
            return true;
        }
        if (constant.getName().equals("none")) {
            return false;
        }
        return false;
    }

    @Override
    protected Boolean evaluate(Operator operator, Iterator<Boolean> operands,
            Object evaluationContext) {
        Boolean o1 = operands.next();
        Boolean o2 = operands.next();
        Boolean result;
        if (operator == OR || operator == OR2 || operator == OR3 || operator == OR4) {
            result = o1 || o2;
        } else if (operator == AND || operator == AND2 || operator == AND3) {
            result = o1 && o2;
        } else if (operator == EXCEPT) {
            result = o1 && (!o2);
        } else {
            throw new IllegalArgumentException();
        }
        return result;
    }

    public boolean evaluateExpression(String expression) {
        try {
            return (Boolean) evaluate(expression);
        } catch (Exception e) {
            //error
        }
        return false;
    }

}
