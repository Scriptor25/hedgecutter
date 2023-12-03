package io.scriptor.hedgecutter.expr;

public class MemExpr extends Expr {

    public final Expr object;
    public final Expr member;

    public MemExpr(Expr object, Expr member) {
        this.object = object;
        this.member = member;
    }

    @Override
    public String toString() {
        return String.format("%s.%s", object, member);
    }

}
