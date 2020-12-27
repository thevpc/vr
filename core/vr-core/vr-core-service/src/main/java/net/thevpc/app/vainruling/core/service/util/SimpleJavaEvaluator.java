package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.DefaultJeep;
import net.thevpc.jeep.core.JFunctionBase;
import net.thevpc.jeep.core.imports.PlatformHelperImports;
import net.thevpc.jeep.core.tokens.JavaIdPattern;
import net.thevpc.jeep.impl.vars.JVarReadOnly;
import net.thevpc.jeep.util.JeepUtils;

import java.util.*;
import net.thevpc.jeep.core.JExpressionOptions;
import net.thevpc.jeep.core.JExpressionUnaryOptions;
import net.thevpc.jeep.core.SimpleJParser;
import net.thevpc.jeep.core.nodes.JSimpleNode;

/**
 * Created by vpc on 4/16/17.
 */
public class SimpleJavaEvaluator implements InSetEvaluator {

    public static final InSetEvaluator INSTANCE = new SimpleJavaEvaluator();
    private final JContext jeep;

    public SimpleJavaEvaluator() {
        jeep = new DefaultJeep();
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
        jeep.tokens().setConfig(tokenizerConfig);
        jeep.parsers().setFactory(new JParserFactory() {
            @Override
            public JParser create(JTokenizer tokenizer, JCompilationUnit compilationUnit, JContext context) {
                final JExpressionOptions<JExpressionOptions> jExpressionOptions = new JExpressionOptions<>();
                final JExpressionUnaryOptions unary = new JExpressionUnaryOptions();
                unary.setExcludedAnnotations(true);
                unary.setExcludedPrefixBraces(true);
                unary.setExcludedPrefixParenthesis(true);
                unary.setExcludedPrefixParenthesis(true);
                jExpressionOptions.setUnary(unary);
                return new SimpleJParser(tokenizer, compilationUnit, context).setDefaultExpressionOptions(jExpressionOptions);
            }
        });
        jeep.operators().declareBinaryOperators("+", "-", "&&", "||");
        JType _boolean = jeep.types().forName("boolean");
        jeep.operators().declareOperatorAlias("et", "&&", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("and", "&&", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("&", "&&", false, _boolean, _boolean);

        jeep.operators().declareOperatorAlias(",", "||", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("ou", "||", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("or", "||", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("|", "||", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("", "||", false, _boolean, _boolean);

        jeep.operators().declareOperatorAlias("sauf", "-", false, _boolean, _boolean);
        jeep.operators().declareOperatorAlias("but", "-", false, _boolean, _boolean);

        jeep.operators().declareListOperator("", _boolean, JOperatorPrecedences.PRECEDENCE_1);

        jeep.vars().declareConst("true", true);
        jeep.vars().declareConst("all", true);
        jeep.vars().declareConst("tous", true);
        jeep.vars().declareConst("false", false);
        jeep.vars().declareConst("aucun", false);
        jeep.vars().declareConst("none", false);

        jeep.resolvers().importType(PlatformHelperImports.class);
        jeep.resolvers().importType(ExtraHelper.class);
        jeep.resolvers().addResolver(new JResolver() {
            @Override
            public JFunction resolveFunction(String name, JTypePattern[] argTypes, JContext context) {
                //if the function was not found, assume it is an 'or'
                // X(Y,Z) becomes X||Y||Z
                JType[] argTypesOk = new JType[argTypes.length];
                Arrays.fill(argTypesOk, _boolean);
                return new JFunctionBase(name, _boolean, argTypesOk, "<runtime>") {
                    @Override
                    public Object invoke(JInvokeContext context) {
                        for (JEvaluable argument : context.getArguments()) {
                            if (JeepUtils.convertToBoolean(argument.evaluate(context))) {
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        });
        jeep.evaluators().setFactory(new JEvaluatorFactory() {
            @Override
            public JEvaluator create(JContext context) {
                return new JEvaluatorImpl();
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
            final JContext ctx = jeep.newContext();
            ctx.vars().declareVar("set", Set.class, items);

            Object ret = ctx.evaluate(expression);
            return JeepUtils.convertToBoolean(ret);
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

    public static class JEvaluatorImpl implements JEvaluator {

        public JEvaluatorImpl() {
        }

        @Override
        public Object evaluate(JNode node, JInvokeContext invokeContext) {
            JSimpleNode jsnode = (JSimpleNode) node;
            switch (jsnode.getNodeTypeName()) {
                case "Identifier": {
                    Set<String> s = (Set<String>) invokeContext.getContext().vars().getValue("set", invokeContext);
                    return s.contains((String) jsnode.getArguments()[0]);
                }
                case "Literal": {
                    return JeepUtils.convertToBoolean(jsnode.getArguments()[0]);
                }
                case "BinaryOperator": {
                    JToken op = (JToken) jsnode.getArguments()[0];
                    JNode left = (JNode) jsnode.getArguments()[1];
                    JNode right = (JNode) jsnode.getArguments()[2];
                    switch (op.image) {
                        case "+": 
                        case "&": 
                        case "&&": 
                        case "et": 
                        case "and": 
                        {
                            if (!JeepUtils.convertToBoolean(evaluate(left, invokeContext))) {
                                return false;
                            }
                            if (!JeepUtils.convertToBoolean(evaluate(right, invokeContext))) {
                                return false;
                            }
                            return true;
                        }
                        case "|": 
                        case "||": 
                        case ",": 
                        case "ou": 
                        case "or": 
                        {
                            if (JeepUtils.convertToBoolean(evaluate(left, invokeContext))) {
                                return true;
                            }
                            if (JeepUtils.convertToBoolean(evaluate(right, invokeContext))) {
                                return true;
                            }
                            return false;
                        }
                        case "-": 
                        case "sauf": 
                        case "but": 
                        {
                            if (!JeepUtils.convertToBoolean(evaluate(left, invokeContext))) {
                                return false;
                            }
                            if (JeepUtils.convertToBoolean(evaluate(right, invokeContext))) {
                                return false;
                            }
                            return true;
                        }
                    }
                    return false;
                }
                case "Pars": {
                    Object[] aa = jsnode.getArguments();
                    for (Object a : aa) {
                        if (JeepUtils.convertToBoolean(evaluate((JNode) a, invokeContext))) {
                            return true;
                        }
                    }
                    return false;
                }
                case "ImplicitOperator": {
                    Object[] aa = jsnode.getArguments();
                    for (Object a : aa) {
                        if (JeepUtils.convertToBoolean(evaluate((JNode) a, invokeContext))) {
                            return true;
                        }
                    }
                    return false;
                }
            }
            return false;
        }
    }

}
