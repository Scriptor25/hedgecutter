package io.scriptor.hedgecutter;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import io.scriptor.hedgecutter.Lexer.Token;
import io.scriptor.hedgecutter.Lexer.TokenType;
import io.scriptor.hedgecutter.expr.BinExpr;
import io.scriptor.hedgecutter.expr.CallExpr;
import io.scriptor.hedgecutter.expr.Expr;
import io.scriptor.hedgecutter.expr.IdExpr;
import io.scriptor.hedgecutter.expr.NumExpr;
import io.scriptor.hedgecutter.expr.StrExpr;
import io.scriptor.hedgecutter.stmt.FuncStmt;
import io.scriptor.hedgecutter.stmt.GiveStmt;
import io.scriptor.hedgecutter.stmt.Stmt;

public class Parser {

    private final Reader mReader;
    private Token mToken;

    public Parser(Reader reader) {
        mReader = reader;
    }

    public Program parse() {
        next();

        final List<Stmt> body = new Vector<>();
        while (!eof())
            body.add(parseStmt());

        return new Program(body.toArray(new Stmt[0]));
    }

    private boolean eof() {
        return mToken.type == TokenType.EOF;
    }

    private Token next() {
        try {
            return mToken = Lexer.next(mReader);
        } catch (IOException e) {
            e.printStackTrace();
            return Token.EOF;
        }
    }

    private boolean at(String value) {
        return mToken.value.equals(value);
    }

    private boolean at(TokenType type) {
        return mToken.type == type;
    }

    private Token expect(String value) {
        if (!at(value))
            throw new RuntimeException();
        return mToken;
    }

    private Token expect(TokenType type) {
        if (!at(type))
            throw new RuntimeException();
        return mToken;
    }

    private Stmt parseStmt() {

        if (at("@"))
            return parseFunctionStmt();

        if (at("give"))
            return parseGiveStmt();

        final var expr = parseExpr();
        expect(";");
        next();

        return expr;
    }

    private FuncStmt parseFunctionStmt() {
        expect("@");
        next();

        final var name = expect(TokenType.IDENTIFIER).value;
        next();
        expect("=");
        next();
        final var type = expect(TokenType.IDENTIFIER).value;
        next();

        final List<Parameter> params = new Vector<>();
        if (at(":")) {
            do {
                next();
                params.add(parseParameter());
            } while (at(","));
        }

        final List<Expr> preconds = new Vector<>();
        if (at("?")) {
            do {
                next();
                preconds.add(parseExpr());
            } while (at(","));
        }

        expect("{");
        next();

        final List<Stmt> body = new Vector<>();
        while (!at("}"))
            body.add(parseStmt());
        next();

        return new FuncStmt(
                type,
                name,
                params.toArray(new Parameter[0]),
                preconds.toArray(new Expr[0]),
                body.toArray(new Stmt[0]));
    }

    private Parameter parseParameter() {
        final var name = expect(TokenType.IDENTIFIER).value;
        next();
        expect("=");
        next();
        boolean array = at("[");
        if (array)
            next();
        final var type = expect(TokenType.IDENTIFIER).value;
        next();
        if (array) {
            expect("]");
            next();
        }
        return new Parameter(new Type(type, array), name);
    }

    private GiveStmt parseGiveStmt() {
        expect("give");
        next();

        if (at(";")) {
            next();
            return new GiveStmt();
        }

        final var expr = parseExpr();
        expect(";");
        next();

        return new GiveStmt(expr);
    }

    private Expr parseExpr() {
        return parseBinCmpExpr();
    }

    private Expr parseBinCmpExpr() {
        var left = parseCallExpr();

        while (at("<") || at(">") || at("=") || at("!")) {
            var operator = mToken.value;
            next();
            if (at("<") || at(">") || at("=") || at("!")) {
                operator += mToken.value;
                next();
            }
            final var right = parseCallExpr();
            left = new BinExpr(left, right, operator);
        }

        return left;
    }

    private Expr parseCallExpr() {
        var callee = parsePrimaryExpr();

        if (at("(")) {
            final List<Expr> args = new Vector<>();
            do {
                next();
                args.add(parseExpr());
            } while (at(","));
            expect(")");
            next();
            callee = new CallExpr(callee, args.toArray(new Expr[0]));
        }

        return callee;
    }

    private Expr parsePrimaryExpr() {
        final var token = mToken;
        next();

        switch (token.type) {
            case IDENTIFIER:
                return new IdExpr(token.value);
            case NUMBER:
                return new NumExpr(token.value);
            case STRING:
                return new StrExpr(token.value);
            case EOF:
                return null;
            case OPERATOR:
                switch (token.value) {
                }
                break;
        }

        throw new RuntimeException();
    }
}
