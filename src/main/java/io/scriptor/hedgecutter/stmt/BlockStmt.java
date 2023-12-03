package io.scriptor.hedgecutter.stmt;

import io.scriptor.Util;

public class BlockStmt extends Stmt {

    public final Stmt[] body;

    public BlockStmt(Stmt[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("{%n%s%n}", Util.toString(true, body));
    }

}
