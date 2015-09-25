/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.api.util;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class InSetEvaluator extends AbstractEvaluator<Boolean> {

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
        // Add the parentheses
        evalParams.addExpressionBracket(BracketPair.PARENTHESES);
        evalParams.addConstants(Arrays.asList(new Constant("all")));
        evalParams.addConstants(Arrays.asList(new Constant("none")));
    }
    private Set<String> items;

    public InSetEvaluator(Set<String> set) {
        super(evalParams);
        this.items = set;
    }

    @Override
    protected Boolean toValue(String item, Object o) {
        if(item.equals("all")){
            return true;
        }
        return items.contains(item.toLowerCase());
    }

    @Override
    protected Boolean evaluate(Constant constant, Object evaluationContext) {
        if(constant.getName().equals("all")){
            return true;
        }
        if(constant.getName().equals("none")){
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
        if (operator == OR || operator == OR2 || operator == OR3) {
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
}
