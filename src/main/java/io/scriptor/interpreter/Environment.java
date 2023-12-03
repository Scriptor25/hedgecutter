package io.scriptor.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.scriptor.Util;
import io.scriptor.interpreter.value.Value;

public class Environment {

    private final Environment mGlobal;
    private final Environment mParent;

    private final Map<String, Map<String, Map<Integer, List<Func>>>> mFunctions;
    private final Map<String, Value> mValues = new HashMap<>();

    public Environment() {
        mGlobal = this;
        mParent = null;
        mFunctions = new HashMap<>();
    }

    public Environment(Environment parent) {
        mGlobal = parent.mGlobal;
        mParent = parent;
        mFunctions = mGlobal.mFunctions;
    }

    public boolean isOrphan() {
        return mParent == null;
    }

    public boolean isGlobal() {
        return mGlobal == this;
    }

    public boolean hasFunc(String name, String member, String... types) {
        if (!mFunctions.containsKey(member) || !mFunctions.get(member).containsKey(name)
                || !mFunctions.get(member).get(name).containsKey(types.length))
            return false;

        final var funcs = mFunctions.get(member).get(name).get(types.length);
        for (final var func : funcs) {
            int i;
            for (i = 0; i < types.length; i++)
                if (!Types.isCompatible(types[i], func.types[i]))
                    break;
            if (i == types.length)
                return true;
        }

        return false;
    }

    public Value execute(String name, Value object, Value... args) {
        final var types = new String[args.length];
        for (int i = 0; i < types.length; i++)
            types[i] = args[i].getType();

        final var member = object == null ? null : object.getType();
        if (!mFunctions.containsKey(member) || !mFunctions.get(member).containsKey(name)
                || !mFunctions.get(member).get(name).containsKey(types.length))
            throw new FormattedException("undefined function %s::%s(%s)", member, name, Util.toString(false, types));

        final var funcs = mFunctions.get(member).get(name).get(types.length);
        for (final var func : funcs) {
            int i;
            for (i = 0; i < types.length; i++)
                if (!Types.isCompatible(types[i], func.types[i]))
                    break;
            if (i == types.length)
                return Interpreter.evaluate(mGlobal, func, object, args);
        }

        throw new FormattedException("undefined function %s::%s(%s)", member, name, Util.toString(false, types));
    }

    public void register(String name, String member, Func func) {
        if (hasFunc(name, member, func.types))
            throw new FormattedException("function %s::%s(%s) already defined", member, name,
                    Util.toString(false, func.types));

        mFunctions
                .computeIfAbsent(member, key -> new HashMap<>())
                .computeIfAbsent(name, key -> new HashMap<>())
                .computeIfAbsent(func.types.length, key -> new Vector<>())
                .add(func);
    }

    public void create(String name, Value value) {
        if (mValues.containsKey(name))
            throw new FormattedException("value '%s' already defined", name);
        mValues.put(name, value);
    }

    public Value get(String name) {
        if (mValues.containsKey(name))
            return mValues.get(name);
        if (isOrphan())
            throw new FormattedException("undefined value '%s'", name);
        return mParent.get(name);
    }

    public void set(String name, Value value) {
        if (mValues.containsKey(name)) {
            if (!Types.isCompatible(value.getType(), mValues.get(name).getType()))
                throw new FormattedException("value of type '%s' cannot be set to type '%s'",
                        value.getType(),
                        mValues.get(name).getType());
            mValues.put(name, value);
            return;
        }
        if (isOrphan())
            throw new FormattedException("undefined value '%s'", name);
        mParent.set(name, value);
    }
}
