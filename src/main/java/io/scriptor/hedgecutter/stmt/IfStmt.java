package io.scriptor.hedgecutter.stmt;

import io.scriptor.hedgecutter.expr.Expr;

public class IfStmt extends Stmt {

    public final Expr condition;
    public final Stmt thenBody;
    public final Stmt elseBody;

    public IfStmt(Expr condition, Stmt thenBody, Stmt elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    @Override
    public String toString() {
        if (elseBody == null)
            return String.format("if (%s)%n%s", condition, thenBody);
        return String.format("if (%s)%n%s%nelse%n%s", condition, thenBody, elseBody);
    }

}
