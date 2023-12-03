package io.scriptor.hedgecutter.stmt;

import io.scriptor.hedgecutter.expr.Expr;

public class VarStmt extends Stmt {

    public final String name;
    public final Expr value;

    public VarStmt(String name, Expr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("$%s = %s;", name, value);
    }

}
