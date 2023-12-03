package io.scriptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.scriptor.hedgecutter.Parser;
import io.scriptor.interpreter.Environment;
import io.scriptor.interpreter.Func;
import io.scriptor.interpreter.Interpreter;
import io.scriptor.interpreter.value.NumValue;
import io.scriptor.interpreter.value.StrValue;
import io.scriptor.interpreter.value.VoidValue;

public class HedgeCutter {

    public static void main(String[] args) throws IOException {
        final var filepath = "hc/test.hc";

        final var reader = new BufferedReader(new FileReader(filepath));
        final var program = new Parser(reader).parse();
        reader.close();

        final var env = new Environment();
        env.register("printf",
                new Func(new String[] { "str", "any" }, new String[] { "fmt", "arg" }, (environment, arguments) -> {
                    final var fmt = arguments[0].asStr();
                    final var arg = arguments[1];
                    System.out.printf(fmt.get(), arg);
                    return VoidValue.create();
                }));

        Interpreter.evaluate(env, program);

        System.out.println(env.execute("main", NumValue.create(0), StrValue.createArray(0)));
    }
}
