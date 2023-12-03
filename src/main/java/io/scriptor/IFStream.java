package io.scriptor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import io.scriptor.interpreter.value.NumValue;
import io.scriptor.interpreter.value.StrValue;
import io.scriptor.interpreter.value.Value;

public class IFStream extends Value {

    private final String mFilename;
    private BufferedReader mReader;

    private IFStream(String filename) {
        mFilename = filename;
        try {
            mReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mReader = null;
        }
    }

    @Override
    protected String type() {
        return "ifstream";
    }

    @Override
    protected boolean bool() {
        return mReader != null;
    }

    @Override
    protected boolean same(Value v) {
        if (!(v instanceof IFStream s))
            return false;
        return mFilename.equals(s.mFilename);
    }

    @Override
    public Value get(NumValue index) {
        return this;
    }

    @Override
    public String toString() {
        return "ifstream<" + mFilename + ">";
    }

    public StrValue readLine() {
        try {
            return StrValue.create(mReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            return StrValue.create("");
        }
    }

    public static IFStream create(String filename) {
        return new IFStream(filename);
    }

}
