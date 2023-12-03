package io.scriptor.interpreter;

public class Types {

    public static final String VOID = "void";
    public static final String ANY = "any";
    public static final String NUM = "num";
    public static final String STR = "str";

    public static boolean isCompatible(String in, String required) {
        return required.equals(ANY) || in.equals(required);
    }
}
