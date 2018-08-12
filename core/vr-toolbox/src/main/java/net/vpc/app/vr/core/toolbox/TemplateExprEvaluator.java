/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.core.toolbox;

import java.util.Date;
import net.vpc.app.vr.core.toolbox.util.JavaUtils;
import net.vpc.common.strings.MessageNameFormat;
import net.vpc.common.strings.StringToObject;
import net.vpc.common.strings.format.AbstractFunction;

/**
 *
 * @author vpc
 */
public class TemplateExprEvaluator {

    private static void configure(MessageNameFormat f, FileSystemTemplater templater) {
        f.register("path", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                return JavaUtils.path(String.valueOf(args[0]));
            }
        });
        f.register("now", new MessageNameFormat.Function() {
            @Override
            public Object eval(MessageNameFormat.ExprNode[] args, MessageNameFormat format, StringToObject provider) {
                return new Date();
            }
        });
        f.register("packageName", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                return JavaUtils.packageName(String.valueOf(args[0]));
            }
        });
        f.register("className", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                return JavaUtils.className(String.valueOf(args[0]));
            }
        });
        f.register("varName", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                return JavaUtils.varName(String.valueOf(args[0]));
            }
        });
        f.register("pathTpPackage", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                return JavaUtils.pathTpPackage(String.valueOf(args[0]));
            }
        });
        f.register("vrMavenModelDependency", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                String module = String.valueOf(args[0]);
                return JavaUtils.vrMavenModelDependency(module, templater.getConfig());
            }
        });
        f.register("vrMavenServiceDependency", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                String module = String.valueOf(args[0]);
                return JavaUtils.vrMavenServiceDependency(module, templater.getConfig());
            }
        });
        f.register("vrMavenWebDependency", new AbstractFunction() {
            @Override
            public Object evalArgs(Object[] args, MessageNameFormat format, StringToObject provider) {
                String module = String.valueOf(args[0]);
                return JavaUtils.vrMavenWebDependency(module, templater.getConfig());
            }
        });
    }

    public static String eval(String expression, FileSystemTemplater templater) {
        MessageNameFormat f = new MessageNameFormat("${" + expression + "}");
        configure(f, templater);
        return f.format(new StringToObject() {
            @Override
            public Object toObject(String string) {
                return templater.getConfig().get(string);
            }
        });
    }
}
