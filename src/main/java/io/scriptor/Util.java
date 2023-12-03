package io.scriptor;

import io.scriptor.hedgecutter.expr.Expr;

public class Util {

    public static String toString(boolean lf, Object[] args) {
        final var builder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            if (i > 0)
                builder.append(lf ? "\r\n" : ", ");
            builder.append(args[i]);
            if (lf && args[i] instanceof Expr)
                builder.append(";");
        }

        return builder.toString();
    }
}
