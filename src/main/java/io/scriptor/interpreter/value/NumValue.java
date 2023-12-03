package io.scriptor.interpreter.value;

import java.util.Arrays;

import io.scriptor.interpreter.Types;

public class NumValue extends Value {

    private double[] mValues;

    private NumValue() {
        super();
    }

    private NumValue(boolean isarray) {
        super(isarray);
    }

    @Override
    protected String type() {
        return Types.NUM;
    }

    @Override
    protected boolean bool() {
        return mValues[0] != 0;
    }

    @Override
    protected boolean same(Value v) {
        if (!(v instanceof NumValue n) || mValues.length != n.mValues.length)
            return false;
        for (int i = 0; i < mValues.length; i++)
            if (get(i) != n.get(i))
                return false;
        return true;
    }

    @Override
    public NumValue get(NumValue index) {
        return NumValue.create(get((int) index.get()));
    }

    @Override
    public String toString() {
        if (!isArray()) {
            final var d = get();
            if (d == Math.floor(d))
                return Long.toString((long) d);
            return Double.toString(get());
        }
        return Arrays.toString(mValues);
    }

    public double get() {
        return mValues[0];
    }

    public double get(int i) {
        return mValues[i];
    }

    public static NumValue create(double value) {
        final var num = new NumValue();
        num.mValues = new double[] { value };
        return num;
    }

    public static NumValue create(boolean value) {
        final var num = new NumValue();
        num.mValues = new double[] { value ? 1.0 : 0.0 };
        return num;
    }

}
