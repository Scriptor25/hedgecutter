package io.scriptor.hedgecutter;

public class Parameter {

    public final Type type;
    public final String name;

    public Parameter(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", name, type);
    }

}
