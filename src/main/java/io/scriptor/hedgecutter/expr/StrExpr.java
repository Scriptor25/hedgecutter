package io.scriptor.hedgecutter.expr;

public class StrExpr extends Expr {

    public final String value;

    public StrExpr(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }
}
