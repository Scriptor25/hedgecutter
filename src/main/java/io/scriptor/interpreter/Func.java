package io.scriptor.interpreter;

import io.scriptor.interpreter.value.Value;

public class Func {

    @FunctionalInterface
    public interface IFunc {
        Value call(Environment env, Value... args);
    }

    public final IFunc func;
    public final String[] types;
    public final String[] names;

    public Func(String[] types, String[] names, IFunc func) {
        this.types = types;
        this.names = names;
        this.func = func;
    }
}
