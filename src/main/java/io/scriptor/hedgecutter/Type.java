package io.scriptor.hedgecutter;

public class Type {

    public final String name;
    public final boolean array;

    public Type(String name, boolean array) {
        this.name = name;
        this.array = array;
    }

    @Override
    public String toString() {
        return array ? "[" + name + "]" : name;
    }
}
