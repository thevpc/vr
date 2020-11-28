package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.DefaultJeep;
import net.thevpc.jeep.core.JFunctionBase;
import net.thevpc.jeep.core.imports.PlatformHelperImports;
import net.thevpc.jeep.core.tokens.JavaIdPattern;
import net.thevpc.jeep.impl.vars.JVarReadOnly;
import net.thevpc.jeep.util.JeepUtils;

import java.util.*;

/**
 * Created by vpc on 4/16/17.
 */
public class SimpleJavaEvaluator implements InSetEvaluator {
    public static final InSetEvaluator INSTANCE = new SimpleJavaEvaluator();
    private final JContext evaluator;

    public SimpleJavaEvaluator() {
        evaluator = new DefaultJeep();
        JTokenConfigBuilder tokenizerConfig = new JTokenConfigBuilder();
        tokenizerConfig.setAcceptIntNumber(false);
        tokenizerConfig.setAcceptFloatNumber(false);
        tokenizerConfig.setIdPattern(new JavaIdPattern() {
            @Override
            public boolean accept(CharSequence prefix, char cc) {
                if (prefix.length() == 0) {
                    return Character.isJavaIdentifierStart(cc);
                }
                return !Character.isWhitespace(cc)
                        && cc != '&' && cc != '|'
                        && cc != '-' && cc != '+'
                        && cc != '(' && cc != ')'
                        && cc != ',';
            }

            @Override
            public boolean valid(CharSequence image) {
                return true;
            }
        });
        evaluator.tokens().setConfig(tokenizerConfig);
        evaluator.operators().declareBinaryOperators("+", "-", "&&", "||");
        JType _boolean = evaluator.types().forName("boolean");
        evaluator.operators().declareOperatorAlias("et", "&&", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("and", "&&", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("&", "&&", false, _boolean, _boolean);

        evaluator.operators().declareOperatorAlias(",", "||", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("ou", "||", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("or", "||", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("|", "||", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("", "||", false, _boolean, _boolean);

        evaluator.operators().declareOperatorAlias("sauf", "-", false, _boolean, _boolean);
        evaluator.operators().declareOperatorAlias("but", "-", false, _boolean, _boolean);

        evaluator.vars().declareConst("true", true);
        evaluator.vars().declareConst("all", true);
        evaluator.vars().declareConst("tous", true);
        evaluator.vars().declareConst("false", false);
        evaluator.vars().declareConst("aucun", false);
        evaluator.vars().declareConst("none", false);

        evaluator.resolvers().importType(PlatformHelperImports.class);
        evaluator.resolvers().importType(ExtraHelper.class);
        evaluator.resolvers().addResolver(new JResolver() {
            //if the var was not found, assume it is a 'false'
            @Override
            public JVar resolveVariable(String name, JContext context) {
                return new JVarReadOnly(name) {

                    @Override
                    public JType type() {
                        return _boolean;
                    }

                    @Override
                    public Object getValue(JInvokeContext evaluator) {
                        Set<String> set = (Set<String>) evaluator.getContext().userProperties().get("set");
                        return set.contains(name);
                    }
                };
            }

            @Override
            public JFunction resolveFunction(String name, JTypePattern[] argTypes, JContext context) {
                return new JFunctionBase() {
                    @Override
                    public Object invoke(JInvokeContext context) {
                        for (JEvaluable argument : context.getArguments()) {
                            
                        }
                        return null;
                    }

                    @Override
                    public String getSourceName() {
                        return null;
                    }
                };
            }

            //if the function was not found, assume it is an 'or'
            // X(Y,Z) becomes X||Y||Z
            @Override
            public Function resolveFunction(String name, ExpressionNode[] args, ExpressionManager context) {
                List<ExpressionNode> all = new ArrayList<ExpressionNode>();
                all.add(JeepFactory.createNameNode(name, _boolean));
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
            expressionEvaluator.getUserProperties().put("set", items);
            //should i prepare var here !!
            return JeepUtils.convertToBoolean(expressionEvaluator.evaluate());
        } catch (Exception ex) {
            return false;
        }
    }

    public static class ExtraHelper {

        public static boolean add(boolean a, boolean b) {
            return a && b;
        }

        public static boolean minus(boolean a, boolean b) {
            return a && !b;
        }
    }
}
