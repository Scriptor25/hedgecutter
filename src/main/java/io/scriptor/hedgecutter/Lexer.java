package io.scriptor.hedgecutter;

import java.io.IOException;
import java.io.Reader;

public class Lexer {

    public enum TokenType {
        IDENTIFIER,
        NUMBER,
        STRING,
        OPERATOR,
        EOF,
    }

    public static class Token {
        public static final Token EOF = new Token(TokenType.EOF, null);

        public final TokenType type;
        public final String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        Token(TokenType type, int value) {
            this.type = type;
            this.value = Character.toString(value);
        }

        @Override
        public String toString() {
            return String.format("%s: %s", type, value);
        }
    }

    private static boolean isIgnore(int c) {
        return c <= 0x20 || c == 0x7F;
    }

    private static boolean isDigit(int c) {
        return c >= 0x30 && c <= 0x39;
    }

    private static boolean isAlpha(int c) {
        return (c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A);
    }

    private static boolean isOperator(int c) {
        return !isIgnore(c) && !isDigit(c) && !isAlpha(c);
    }

    public static Token next(Reader reader) throws IOException {
        var c = reader.read();
        if (c < 0)
            return Token.EOF;

        while (isIgnore(c) && c >= 0)
            c = reader.read();
        if (c < 0)
            return Token.EOF;

        if (c == '#') {
            while (c != '\n' && c >= 0)
                c = reader.read();
            return next(reader);
        }

        if (c == '"') {
            final var builder = new StringBuilder();
            while (c >= 0) {
                c = reader.read();
                if (c == '"')
                    break;
                builder.append((char) c);
            }
            return new Token(TokenType.STRING, builder.toString());
        }

        if (isDigit(c)) {
            final var builder = new StringBuilder();
            while (c >= 0) {
                builder.append((char) c);
                reader.mark(1);
                c = reader.read();
                if (c != '.' && (isOperator(c) || isIgnore(c))) {
                    reader.reset();
                    break;
                }
            }
            return new Token(TokenType.NUMBER, builder.toString());
        }

        if (isAlpha(c)) {
            final var builder = new StringBuilder();
            while (c >= 0) {
                builder.append((char) c);
                reader.mark(1);
                c = reader.read();
                if (!(isAlpha(c) || isDigit(c))) {
                    reader.reset();
                    break;
                }
            }
            return new Token(TokenType.IDENTIFIER, builder.toString());
        }

        return new Token(TokenType.OPERATOR, c);
    }
}
