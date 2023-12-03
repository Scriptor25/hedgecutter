package io.scriptor.hedgecutter.stmt;

import io.scriptor.hedgecutter.expr.Expr;

public class GiveStmt extends Stmt {

    public final Expr value;

    public GiveStmt() {
        this.value = null;
    }

    public GiveStmt(Expr value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("give %s;", value);
    }
}
