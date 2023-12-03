package io.scriptor.hedgecutter.stmt;

import io.scriptor.Util;
import io.scriptor.hedgecutter.Parameter;
import io.scriptor.hedgecutter.expr.Expr;

public class FuncStmt extends Stmt {

    public final String type;
    public final String name;
    public final Parameter[] parameters;
    public final Expr[] preconditions;
    public final Stmt[] body;

    public FuncStmt(String type, String name, Parameter[] params, Expr[] preconds, Stmt[] body) {
        this.type = type;
        this.name = name;
        this.parameters = params;
        this.preconditions = preconds;
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("@%s = %s : %s ? %s %n{%n%s%n}",
                name,
                type,
                Util.toString(false, parameters),
                Util.toString(false, preconditions),
                Util.toString(true, body));
    }
}
