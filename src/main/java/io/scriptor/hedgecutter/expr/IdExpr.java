package io.scriptor.hedgecutter.expr;

public class IdExpr extends Expr {

    public final String name;

    public IdExpr(String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
