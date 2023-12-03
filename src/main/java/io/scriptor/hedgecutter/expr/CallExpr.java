package io.scriptor.hedgecutter.expr;

import io.scriptor.Util;

public class CallExpr extends Expr {

    public final Expr callee;
    public final Expr[] arguments;

    public CallExpr(Expr callee, Expr[] args) {
        this.callee = callee;
        this.arguments = args;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", callee, Util.toString(false, arguments));
    }

}
