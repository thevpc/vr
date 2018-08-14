package net.vpc.app.vainruling.core.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.nodes.ExpressionNodeVariableName;
import net.vpc.common.strings.StringUtils;

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

    private final DefaultExpressionEvaluator evaluator;

    private Set<String> items;

    public SimpleJavaEvaluator(Set<String> set) {
        this.items = new HashSet<>();
        for (String string : set) {
            this.items.add(StringUtils.normalize(string).toLowerCase());
        }
        evaluator = new DefaultExpressionEvaluator();
        ExpressionStreamTokenizerConfig tokenizerConfig = new ExpressionStreamTokenizerConfig();
        tokenizerConfig.setAcceptIntNumber(false);
        tokenizerConfig.setAcceptFloatNumber(false);
        tokenizerConfig.setAcceptComplexNumber(false);
        tokenizerConfig.setIdentifierFilter(new IdentifierFilter() {

            @Override
            public boolean isIdentifierPart(char cc) {
                return !Character.isWhitespace(cc)
                        && cc != '&' && cc != '|'
                        && cc != '-' && cc != '+'
                        && cc != '(' && cc != ')'
                        && cc != ',';
            }

            @Override
            public boolean isIdentifierStart(char cc) {
                return isIdentifierPart(cc);
            }

        });
        evaluator.setTokenizerConfig(tokenizerConfig);
        evaluator.declareBinaryOperators("+", "-", "&&", "||");

        evaluator.declareOperatorAlias("et", "&&", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("and", "&&", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("&", "&&", Boolean.TYPE, Boolean.TYPE);

        evaluator.declareOperatorAlias(",", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("ou", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("or", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("|", "||", Boolean.TYPE, Boolean.TYPE);
        evaluator.declareOperatorAlias("", "||", Boolean.TYPE, Boolean.TYPE);

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
        evaluator.addResolver(new ExpressionEvaluatorResolver() {
            //if the var was not found, assume it is a 'false'
            @Override
            public Variable resolveVariable(String name, ExpressionEvaluator context) {
                return JeepFactory.createVar(name, false);
            }

            //if the function was not found, assume it is an 'or'
            // X(Y,Z) becomes X||Y||Z
            @Override
            public Function resolveFunction(String name, ExpressionNode[] args, ExpressionEvaluator context) {
                List<ExpressionNode> all = new ArrayList<ExpressionNode>();
                all.add(JeepFactory.createNameNode(name, Boolean.TYPE));
                all.addAll(Arrays.asList(args));
                Class[] argTypes = context.getExprTypes(all.toArray(new ExpressionNode[all.size()]));
                return new FunctionBase(name, Boolean.class, argTypes) {
                    @Override
                    public Object evaluate(ExpressionNode[] args, ExpressionEvaluator evaluator) {
                        for (ExpressionNode arg : args) {
                            if (JeepUtils.convertToBoolean(evaluator.evaluate(arg))) {
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        });
        for (String item : items) {
            evaluator.declareVar(item, Boolean.TYPE, true);
        }
    }

    @Override
    public boolean evaluateExpression(String expression) {
        expression = StringUtils.normalize(expression).toLowerCase();
        try {
            return JeepUtils.convertToBoolean(evaluator.evaluate(expression));
        } catch (Exception ex) {
            return false;
        }
    }
}
