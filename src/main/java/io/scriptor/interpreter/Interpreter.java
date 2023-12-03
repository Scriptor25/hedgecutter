package io.scriptor.interpreter;

import io.scriptor.hedgecutter.Program;
import io.scriptor.hedgecutter.expr.BinExpr;
import io.scriptor.hedgecutter.expr.CallExpr;
import io.scriptor.hedgecutter.expr.Expr;
import io.scriptor.hedgecutter.expr.IdExpr;
import io.scriptor.hedgecutter.expr.NumExpr;
import io.scriptor.hedgecutter.expr.StrExpr;
import io.scriptor.hedgecutter.stmt.FuncStmt;
import io.scriptor.hedgecutter.stmt.GiveStmt;
import io.scriptor.hedgecutter.stmt.Stmt;
import io.scriptor.interpreter.value.NumValue;
import io.scriptor.interpreter.value.StrValue;
import io.scriptor.interpreter.value.Value;
import io.scriptor.interpreter.value.VoidValue;

public class Interpreter {

    public static Value evaluate(Environment env, Program program) {
        for (final var stmt : program.body)
            evaluate(env, stmt);

        return null;
    }

    public static Value evaluate(Environment env, Stmt stmt) {
        if (stmt instanceof FuncStmt s)
            return evaluate(env, s);
        if (stmt instanceof GiveStmt s)
            return evaluate(env, s);

        if (stmt instanceof Expr s)
            return evaluate(env, s);

        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, FuncStmt stmt) {
        final var types = new String[stmt.parameters.length];
        final var names = new String[stmt.parameters.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = stmt.parameters[i].type.toString();
            names[i] = stmt.parameters[i].name;
        }

        final Func.IFunc func = (environment, args) -> {
            // check preconditions
            for (final var expr : stmt.preconditions)
                if (!evaluate(environment, expr).asBool())
                    throw new FormattedException("precondition '%s' returned false", expr);

            // evaluate body
            Value value = null;
            for (final var s : stmt.body) {
                value = evaluate(environment, s);
                if (value.isReturn()) {
                    value.setReturn(false);
                    break;
                }
            }

            // check return type
            if (value == null) {
                if (!stmt.type.equals(Types.VOID))
                    throw new FormattedException("function of type '%s' cannot return type 'void'", stmt.type);
                value = VoidValue.create(); // to avoid null pointer exceptions
            } else if (!Types.isCompatible(value.getType(), stmt.type))
                throw new FormattedException("function of type '%s' cannot return type '%s'",
                        stmt.type,
                        value.getType());

            // return
            return value;
        };

        env.register(stmt.name, new Func(types, names, func));

        return null;
    }

    public static Value evaluate(Environment env, GiveStmt stmt) {
        return evaluate(env, stmt.value).setReturn(true);
    }

    public static Value evaluate(Environment env, Expr expr) {
        if (expr instanceof BinExpr e)
            return evaluate(env, e);
        if (expr instanceof CallExpr e)
            return evaluate(env, e);
        if (expr instanceof IdExpr e)
            return evaluate(env, e);
        if (expr instanceof NumExpr e)
            return evaluate(env, e);
        if (expr instanceof StrExpr e)
            return evaluate(env, e);

        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, BinExpr expr) {
        final var left = evaluate(env, expr.left);
        final var right = evaluate(env, expr.right);

        switch (expr.operator) {
            case "+":
                return Value.operatorAdd(left, right);
            case "-":
                return Value.operatorSub(left, right);
            case "*":
                return Value.operatorMul(left, right);
            case "/":
                return Value.operatorDiv(left, right);
            case "%":
                return Value.operatorMod(left, right);
            case "<":
                return Value.operatorCmpL(left, right);
            case ">":
                return Value.operatorCmpG(left, right);
            case "<=":
                return Value.operatorCmpLE(left, right);
            case ">=":
                return Value.operatorCmpGE(left, right);
            case "==":
                return Value.operatorCmpE(left, right);
            case "!=":
                return Value.operatorCmpNE(left, right);
        }

        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, CallExpr expr) {
        String name = null;
        if (expr.callee instanceof IdExpr e)
            name = e.name;
        else
            throw new UnsupportedOperationException();

        final var args = new Value[expr.arguments.length];
        for (int i = 0; i < args.length; i++)
            args[i] = evaluate(env, expr.arguments[i]);

        return env.execute(name, args);
    }

    public static Value evaluate(Environment env, IdExpr expr) {
        return env.get(expr.name);
    }

    public static Value evaluate(Environment env, NumExpr expr) {
        return NumValue.create(expr.value);
    }

    public static Value evaluate(Environment env, StrExpr expr) {
        return StrValue.create(expr.value);
    }

    public static Value evaluate(Environment env, Func func, Value... args) {
        final var environment = new Environment(env);

        // put args into environment
        for (int i = 0; i < func.names.length; i++)
            environment.create(func.names[i], args[i]);

        return func.func.call(environment, args);
    }

}
