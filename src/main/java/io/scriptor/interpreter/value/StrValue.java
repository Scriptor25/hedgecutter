package io.scriptor.interpreter.value;

import java.util.Arrays;

import io.scriptor.interpreter.Types;

public class StrValue extends Value {

    private String[] mValues;

    private StrValue() {
        super();
    }

    private StrValue(boolean isarray) {
        super(isarray);
    }

    @Override
    protected String type() {
        return Types.STR;
    }

    @Override
    protected boolean bool() {
        return !mValues[0].isEmpty();
    }

    @Override
    protected boolean same(Value v) {
        if (!(v instanceof StrValue n) || mValues.length != n.mValues.length)
            return false;
        for (int i = 0; i < mValues.length; i++)
            if (get(i) != n.get(i))
                return false;
        return true;
    }

    @Override
    public String toString() {
        if (!isArray())
            return get();
        return Arrays.toString(mValues);
    }

    public String get() {
        return mValues[0];
    }

    public String get(int i) {
        return mValues[i];
    }

    public static StrValue createArray(int size) {
        final var str = new StrValue(true);
        str.mValues = new String[size];
        return str;
    }

    public static Value create(String value) {
        final var str = new StrValue();
        str.mValues = new String[] { value };
        return str;
    }

}
