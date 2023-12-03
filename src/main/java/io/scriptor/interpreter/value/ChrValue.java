package io.scriptor.interpreter.value;

import io.scriptor.interpreter.Types;

public class ChrValue extends Value {

    private char mValue;

    private ChrValue() {
        super();
    }

    @Override
    protected String type() {
        return Types.CHR;
    }

    @Override
    protected boolean bool() {
        return mValue != (char) 0;
    }

    @Override
    protected boolean same(Value v) {
        if (!(v instanceof ChrValue c))
            return false;
        return mValue == c.mValue;
    }

    @Override
    public Value get(NumValue index) {
        return this;
    }

    @Override
    public String toString() {
        return Character.toString(mValue);
    }

    public char get() {
        return mValue;
    }

    public static ChrValue create(char value) {
        final var chr = new ChrValue();
        chr.mValue = value;
        return chr;
    }

}
