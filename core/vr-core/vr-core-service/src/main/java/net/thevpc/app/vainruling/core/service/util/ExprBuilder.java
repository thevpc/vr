/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.upa.expressions.And;
import net.thevpc.upa.expressions.Cst;
import net.thevpc.upa.expressions.Equals;
import net.thevpc.upa.expressions.Expression;
import net.thevpc.upa.expressions.Literal;
import net.thevpc.upa.expressions.Param;
import net.thevpc.upa.expressions.Var;

/**
 *
 * @author vpc
 */
public class ExprBuilder {

    private Expression e;

    public ExprBuilder(Expression e) {
        this.e = e;
    }

    public static ExprBuilder ofVar(String v) {
        return new ExprBuilder(new Var(v));
    }

    public static ExprBuilder ofCst(Object v) {
        return new ExprBuilder(new Cst(v));
    }

    public static ExprBuilder ofParam(String p, Object v) {
        return new ExprBuilder(new Param(p, v));
    }

    public static ExprBuilder ofLiteral(Object v) {
        return new ExprBuilder(new Literal(v,null));
    }

    public ExprBuilder dotVar(String v) {
        return new ExprBuilder(new Var(e, v));
    }

    public ExprBuilder eq(ExprBuilder o) {
        return new ExprBuilder(new Equals(e, o.build()));
    }

    public ExprBuilder and(ExprBuilder o) {
        return new ExprBuilder(And.create(e, o.build()));
    }

    public Expression build() {
        return e;
    }
}
