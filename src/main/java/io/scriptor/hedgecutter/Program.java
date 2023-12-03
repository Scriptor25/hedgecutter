package io.scriptor.hedgecutter;

import io.scriptor.Util;
import io.scriptor.hedgecutter.stmt.Stmt;

public class Program {

    public final Stmt[] body;

    public Program(Stmt[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("%s", Util.toString(true, body));
    }
}
