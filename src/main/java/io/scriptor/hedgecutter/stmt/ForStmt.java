package io.scriptor.hedgecutter.stmt;

import io.scriptor.hedgecutter.expr.Expr;

public class ForStmt extends Stmt {

    public final Stmt begin;
    public final Expr condition;
    public final Stmt loop;
    public final Stmt body;

    public ForStmt(Stmt begin, Expr condition, Stmt loop, Stmt body) {
        this.begin = begin;
        this.condition = condition;
        this.loop = loop;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("for (%s; %s; %s)%n%s", begin, condition, loop, body);
    }

}
