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
            body.add(parseStmt(true));

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

    private Stmt parseStmt(boolean end) {

        if (at("{"))
            return parseBlockStmt();

        if (at("for"))
            return parseForStmt();

        if (at("@"))
            return parseFunctionStmt();

        if (at("give"))
            return parseGiveStmt(end);

        if (at("if"))
            return parseIfStmt();

        if (at("$"))
            return parseVarStmt(end);

        final var expr = parseExpr();
        if (end) {
            expect(";");
            next();
        }

        return expr;
    }

    private BlockStmt parseBlockStmt() {
        expect("{");
        next();

        final List<Stmt> body = new Vector<>();
        while (!at("}"))
            body.add(parseStmt(true));
        next();

        return new BlockStmt(body.toArray(new Stmt[0]));
    }

    private ForStmt parseForStmt() {
        expect("for");
        next();
        expect("(");
        next();
        Stmt begin = null;
        if (!at(";"))
            begin = parseStmt(false);
        expect(";");
        next();
        Expr condition = null;
        if (!at(";"))
            condition = parseExpr();
        expect(";");
        next();
        Stmt loop = null;
        if (!at(";"))
            loop = parseStmt(false);
        expect(")");
        next();
        return new ForStmt(begin, condition, loop, parseStmt(true));
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

        return new FuncStmt(
                type,
                name,
                params.toArray(new Parameter[0]),
                preconds.toArray(new Expr[0]),
                parseBlockStmt());
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

    private GiveStmt parseGiveStmt(boolean end) {
        expect("give");
        next();

        if (at(";")) {
            next();
            return new GiveStmt();
        }

        final var expr = parseExpr();
        if (end) {
            expect(";");
            next();
        }

        return new GiveStmt(expr);
    }

    private IfStmt parseIfStmt() {
        expect("if");
        next();
        expect("(");
        next();
        final var condition = parseExpr();
        expect(")");
        next();
        final var thenStmt = parseStmt(true);

        if (!at("else")) {
            return new IfStmt(condition, thenStmt, null);
        }

        expect("else");
        next();

        return new IfStmt(condition, thenStmt, parseStmt(true));
    }

    private VarStmt parseVarStmt(boolean end) {
        expect("$");
        next();
        final var name = expect(TokenType.IDENTIFIER).value;
        next();
        expect("=");
        next();
        final var value = parseExpr();
        if (end) {
            expect(";");
            next();
        }

        return new VarStmt(name, value);
    }

    private Expr parseExpr() {
        return parseBinCmpExpr();
    }

    private Expr parseBinCmpExpr() {
        var left = parseBinSumExpr();

        while (at("<") || at(">") || at("=") || at("!")) {
            var operator = mToken.value;
            next();
            if (at("<") || at(">") || at("=") || at("!")) {
                operator += mToken.value;
                next();
            }

            if (operator.equals("=")) {
                return new BinExpr(left, parseExpr(), "=");
            }

            left = new BinExpr(left, parseBinSumExpr(), operator);
        }

        return left;
    }

    private Expr parseBinSumExpr() {
        var left = parseIndexExpr();

        while (at("+") || at("-")) {
            var operator = mToken.value;
            next();

            if (at("=")) {
                next();
                return new BinExpr(left, new BinExpr(left, parseExpr(), operator), "="); // x += y --> x = x + y
            }

            if (at(operator)) {
                next();
                return new BinExpr(left, new BinExpr(left, new NumExpr(1), operator), "="); // x++ --> x = x + 1
            }

            left = new BinExpr(left, parseIndexExpr(), operator);
        }

        return left;
    }

    private Expr parseIndexExpr() {
        var array = parseCallExpr();

        if (at("[")) {
            next();
            array = new IndexExpr(array, parseExpr());
            expect("]");
            next();
        }

        return array;
    }

    private Expr parseCallExpr() {
        var callee = parseMemExpr();

        if (at("(")) {
            next();
            final List<Expr> args = new Vector<>();
            while (!at(")") && !eof()) {
                args.add(parseExpr());
                if (!at(","))
                    break;
                expect(",");
                next();
            }
            expect(")");
            next();
            callee = new CallExpr(callee, args.toArray(new Expr[0]));
        }

        return callee;
    }

    private Expr parseMemExpr() {
        var object = parsePrimaryExpr();

        while (at(".")) {
            next();
            object = new MemExpr(object, parsePrimaryExpr());
        }

        return object;
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
                    case "!":
                    case "-":
                    case "~":
                    case "&":
                    case "*":
                        return new UnExpr(token.value, parseIndexExpr());
                }
                break;
        }

        throw new RuntimeException();
    }
}
