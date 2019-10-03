package net.vpc.app.vainruling.core.service.util;

import java.util.*;

import net.vpc.common.jeep.*;
import net.vpc.common.strings.StringUtils;

/**
 * Created by vpc on 4/16/17.
 */
public class SimpleJavaEvaluator implements InSetEvaluator {
    public static final InSetEvaluator INSTANCE=new SimpleJavaEvaluator();
    public static class ExtraHelper {

        public static boolean add(boolean a, boolean b) {
            return a && b;
        }

        public static boolean minus(boolean a, boolean b) {
            return a && !b;
        }
    }

    private final DefaultExpressionManager evaluator;


    public SimpleJavaEvaluator() {
        evaluator = new DefaultExpressionManager();
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
        evaluator.addResolver(new AbstractExpressionEvaluatorResolver() {
            //if the var was not found, assume it is a 'false'
            @Override
            public Variable resolveVariable(String name, ExpressionManager context) {
                return new ReadOnlyVariable(name) {
                    @Override
                    public Class getType() {
                        return Boolean.TYPE;
                    }

                    @Override
                    public Object getValue(ExpressionEvaluator evaluator) {
                        Set<String> set=(Set<String>) evaluator.getUserProperties().get("set");
                        return set.contains(name);
                    }
                };
            }

            //if the function was not found, assume it is an 'or'
            // X(Y,Z) becomes X||Y||Z
            @Override
            public Function resolveFunction(String name, ExpressionNode[] args, ExpressionManager context) {
                List<ExpressionNode> all = new ArrayList<ExpressionNode>();
                all.add(JeepFactory.createNameNode(name, Boolean.TYPE));
                all.addAll(Arrays.asList(args));
                Class[] argTypes = context.getExprTypes(all.toArray(new ExpressionNode[all.size()]));
                return new FunctionBase(name, Boolean.class, argTypes) {
                    @Override
                    public Object evaluate(ExpressionNode[] args, ExpressionEvaluator evaluator) {
                        for (ExpressionNode arg : args) {
                            if (JeepUtils.convertToBoolean(arg.evaluate(evaluator))) {
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        });
    }

    @Override
    public boolean evaluateExpression(String expression, Collection<String> set) {
        Set<String> items = new HashSet<>();
        for (String string : set) {
            items.add(VrUtils.normalizeName(string));
        }

        expression = VrUtils.normalizeName(expression);
        try {
            ExpressionEvaluator expressionEvaluator = evaluator.createEvaluator(expression);
            expressionEvaluator.getUserProperties().put("set",items);
            //should i prepare var here !!
            return JeepUtils.convertToBoolean(expressionEvaluator.evaluate());
        } catch (Exception ex) {
            return false;
        }
    }
}
