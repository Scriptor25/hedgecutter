package io.scriptor.interpreter;

import io.scriptor.hedgecutter.Program;
import io.scriptor.hedgecutter.expr.BinExpr;
import io.scriptor.hedgecutter.expr.CallExpr;
import io.scriptor.hedgecutter.expr.Expr;
import io.scriptor.hedgecutter.expr.IdExpr;
import io.scriptor.hedgecutter.expr.IndexExpr;
import io.scriptor.hedgecutter.expr.MemExpr;
import io.scriptor.hedgecutter.expr.NumExpr;
import io.scriptor.hedgecutter.expr.StrExpr;
import io.scriptor.hedgecutter.expr.UnExpr;
import io.scriptor.hedgecutter.stmt.BlockStmt;
import io.scriptor.hedgecutter.stmt.ForStmt;
import io.scriptor.hedgecutter.stmt.FuncStmt;
import io.scriptor.hedgecutter.stmt.GiveStmt;
import io.scriptor.hedgecutter.stmt.IfStmt;
import io.scriptor.hedgecutter.stmt.Stmt;
import io.scriptor.hedgecutter.stmt.VarStmt;
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
        if (stmt instanceof BlockStmt s)
            return evaluate(env, s);
        if (stmt instanceof ForStmt s)
            return evaluate(env, s);
        if (stmt instanceof FuncStmt s)
            return evaluate(env, s);
        if (stmt instanceof GiveStmt s)
            return evaluate(env, s);
        if (stmt instanceof IfStmt s)
            return evaluate(env, s);
        if (stmt instanceof VarStmt s)
            return evaluate(env, s);

        if (stmt instanceof Expr s)
            return evaluate(env, s);

        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, BlockStmt stmt) {
        Value value = null;
        final var e = new Environment(env);
        for (final var s : stmt.body) {
            value = evaluate(e, s);
            if (value != null && value.isReturn())
                break;
        }
        return value;
    }

    public static Value evaluate(Environment env, ForStmt stmt) {
        Value value = null;
        final var e = new Environment(env);
        for (evaluate(e, stmt.begin); evaluate(e, stmt.condition).asBool(); evaluate(e, stmt.loop)) {
            value = evaluate(e, stmt.body);
            if (value != null && value.isReturn())
                break;
        }
        return value;
    }

    public static Value evaluate(Environment env, FuncStmt stmt) {
        final var types = new String[stmt.parameters.length];
        final var names = new String[stmt.parameters.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = stmt.parameters[i].type.toString();
            names[i] = stmt.parameters[i].name;
        }

        final Func.IFunc func = (environment, object, args) -> {
            // check preconditions
            for (final var expr : stmt.preconditions)
                if (!evaluate(environment, expr).asBool())
                    throw new FormattedException("precondition '%s' returned false", expr);

            // evaluate body
            Value value = null;
            for (final var s : stmt.body) {
                value = evaluate(environment, s);
                if (value != null && value.isReturn()) {
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

        env.register(stmt.name, null, new Func(types, names, func));

        return null;
    }

    public static Value evaluate(Environment env, GiveStmt stmt) {
        return evaluate(env, stmt.value).setReturn(true);
    }

    public static Value evaluate(Environment env, IfStmt stmt) {
        return evaluate(env, stmt.condition).asBool()
                ? evaluate(env, stmt.thenBody)
                : (stmt.elseBody != null) ? evaluate(env, stmt.elseBody) : null;
    }

    public static Value evaluate(Environment env, VarStmt stmt) {
        env.create(stmt.name, evaluate(env, stmt.value));
        return null;
    }

    public static Value evaluate(Environment env, Expr expr) {
        if (expr instanceof BinExpr e)
            return evaluate(env, e);
        if (expr instanceof CallExpr e)
            return evaluate(env, e);
        if (expr instanceof IdExpr e)
            return evaluate(env, e);
        if (expr instanceof IndexExpr e)
            return evaluate(env, e);
        if (expr instanceof MemExpr e)
            return evaluate(env, e);
        if (expr instanceof NumExpr e)
            return evaluate(env, e);
        if (expr instanceof StrExpr e)
            return evaluate(env, e);
        if (expr instanceof UnExpr e)
            return evaluate(env, e);

        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, BinExpr expr) {
        if (expr.operator.equals("=")) {
            final var value = evaluate(env, expr.right);
            env.set(((IdExpr) expr.left).name, value);
            return value;
        }

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
        Value object = null;
        if (expr.callee instanceof IdExpr e)
            name = e.name;
        else if (expr.callee instanceof MemExpr e) {
            name = ((IdExpr) e.member).name;
            object = evaluate(env, e.object);
        } else
            throw new UnsupportedOperationException();

        final var args = new Value[expr.arguments.length];
        for (int i = 0; i < args.length; i++)
            args[i] = evaluate(env, expr.arguments[i]);

        return env.execute(name, object, args);
    }

    public static Value evaluate(Environment env, IdExpr expr) {
        return env.get(expr.name);
    }

    public static Value evaluate(Environment env, IndexExpr expr) {
        final var index = evaluate(env, expr.index);
        final var array = evaluate(env, expr.array);
        if (!array.isArray())
            throw new FormattedException("cannot index into non-array value");
        if (!index.isNum())
            throw new FormattedException("cannot index into array with non-number value");
        return array.get(index.asNum());
    }

    public static Value evaluate(Environment env, MemExpr expr) {
        final var object = evaluate(env, expr.object);
        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, NumExpr expr) {
        return NumValue.create(expr.value);
    }

    public static Value evaluate(Environment env, StrExpr expr) {
        return StrValue.create(expr.value);
    }

    public static Value evaluate(Environment env, UnExpr expr) {
        final var value = evaluate(env, expr.value);

        switch (expr.operator) {
            case "!":
                return Value.operatorNot(value);
        }

        throw new UnsupportedOperationException();
    }

    public static Value evaluate(Environment env, Func func, Value object, Value... args) {
        final var environment = new Environment(env);

        if (object != null)
            environment.create("this", object);

        // put args into environment
        for (int i = 0; i < func.names.length; i++)
            environment.create(func.names[i], args[i]);

        return func.func.call(environment, object, args);
    }

}
