package io.scriptor.interpreter.value;

public abstract class Value {

    private final boolean mIsArray;
    private boolean mReturn;

    protected Value() {
        mIsArray = false;
    }

    protected Value(boolean isarray) {
        mIsArray = isarray;
    }

    protected abstract String type();

    protected abstract boolean bool();

    protected abstract boolean same(Value v);

    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Value v))
            return false;
        return same(v);
    }

    public boolean isArray() {
        return mIsArray;
    }

    public String getType() {
        final var type = type();
        return mIsArray ? "[" + type + "]" : type;
    }

    public boolean isReturn() {
        return mReturn;
    }

    public Value setReturn(boolean r) {
        mReturn = r;
        return this;
    }

    public boolean isNum() {
        return this instanceof NumValue;
    }

    public boolean asBool() {
        if (isArray())
            return true;
        return bool();
    }

    public NumValue asNum() {
        return (NumValue) this;
    }

    public StrValue asStr() {
        return (StrValue) this;
    }

    public static Value operatorAdd(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorSub(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorMul(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorDiv(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorMod(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorCmpL(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorCmpG(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorCmpLE(Value left, Value right) {
        throw new UnsupportedOperationException();
    }

    public static Value operatorCmpGE(Value left, Value right) {
        if (left.isNum() && right.isNum())
            return NumValue.create(left.asNum().get() >= right.asNum().get());

        throw new UnsupportedOperationException();
    }

    public static Value operatorCmpE(Value left, Value right) {
        return NumValue.create(left.equals(right));
    }

    public static Value operatorCmpNE(Value left, Value right) {
        return NumValue.create(!left.equals(right));
    }

}
