package io.scriptor.hedgecutter.expr;

public class IndexExpr extends Expr {

    public final Expr array;
    public final Expr index;

    public IndexExpr(Expr array, Expr index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", array, index);
    }

}
