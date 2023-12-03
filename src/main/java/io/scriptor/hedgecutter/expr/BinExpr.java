package io.scriptor.hedgecutter.expr;

public class BinExpr extends Expr {

    public final Expr left;
    public final Expr right;
    public final String operator;

    public BinExpr(Expr left, Expr right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", left, operator, right);
    }

}
