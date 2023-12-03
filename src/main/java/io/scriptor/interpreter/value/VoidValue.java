package io.scriptor.interpreter.value;

import io.scriptor.interpreter.Types;

public class VoidValue extends Value {

    private VoidValue() {
        super();
    }

    @Override
    protected String type() {
        return Types.VOID;
    }

    @Override
    protected boolean bool() {
        return false;
    }

    @Override
    protected boolean same(Value v) {
        return true;
    }

    @Override
    public VoidValue get(NumValue index) {
        return VoidValue.create();
    }

    @Override
    public String toString() {
        return "";
    }

    public static VoidValue create() {
        return new VoidValue();
    }

}
