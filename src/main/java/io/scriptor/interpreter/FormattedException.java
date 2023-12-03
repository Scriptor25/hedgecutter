package io.scriptor.interpreter;

public class FormattedException extends RuntimeException {

    public FormattedException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }
}
