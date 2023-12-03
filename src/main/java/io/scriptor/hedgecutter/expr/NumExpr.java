package io.scriptor.hedgecutter.expr;

public class NumExpr extends Expr {

    public final double value;

    public NumExpr(double value) {
        this.value = value;
    }

    public NumExpr(String value) {
        this.value = Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
