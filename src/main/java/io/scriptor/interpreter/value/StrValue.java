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
    public StrValue get(NumValue index) {
        return StrValue.create(get((int) index.get()));
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

    public static StrValue create(String value) {
        final var str = new StrValue();
        str.mValues = new String[] { value == null ? "" : value };
        return str;
    }

    public static StrValue createArray(int size) {
        final var str = new StrValue(true);
        str.mValues = new String[size];
        return str;
    }

    public static StrValue createArray(String[] values, int offset, int size) {
        final var str = new StrValue(true);
        str.mValues = new String[size];
        for (int i = 0; i < size; i++)
            str.mValues[i] = values[i + offset];
        return str;
    }

}
