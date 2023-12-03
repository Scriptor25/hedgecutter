package io.scriptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.scriptor.hedgecutter.Parser;
import io.scriptor.interpreter.Environment;
import io.scriptor.interpreter.Func;
import io.scriptor.interpreter.Interpreter;
import io.scriptor.interpreter.Types;
import io.scriptor.interpreter.value.ChrValue;
import io.scriptor.interpreter.value.NumValue;
import io.scriptor.interpreter.value.StrValue;
import io.scriptor.interpreter.value.VoidValue;

public class HedgeCutter {

    public static void main(String[] args) throws IOException {
        final var filepath = args[0];

        final var reader = new BufferedReader(new FileReader(filepath));
        final var program = new Parser(reader).parse();
        reader.close();

        final var env = new Environment();
        env.register("printf", null,
                new Func(new String[] { Types.STR, Types.ANY }, new String[] { "fmt", "arg" },
                        (environment, object, arguments) -> {
                            final var fmt = arguments[0].asStr();
                            final var arg = arguments[1];
                            System.out.printf(fmt.get(), arg);
                            return VoidValue.create();
                        }));
        env.register("isdigit", null,
                new Func(new String[] { Types.CHR }, new String[] { "x" },
                        (environment, object, arguments) -> {
                            final var x = (ChrValue) arguments[0];
                            return NumValue.create(Character.isDigit(x.get()));
                        }));
        env.register("num", null,
                new Func(new String[] { Types.STR }, new String[] { "x" },
                        (environment, object, arguments) -> {
                            final var x = (StrValue) arguments[0];
                            return NumValue.create(Double.parseDouble(x.get()));
                        }));

        env.register("size", Types.STR,
                new Func(new String[0], new String[0],
                        (environment, object, arguments) -> {
                            final var str = object.asStr();
                            return NumValue.create(str.get().length());
                        }));
        env.register("get", Types.STR,
                new Func(new String[] { Types.NUM }, new String[] { "i" },
                        (environment, object, arguments) -> {
                            final var str = object.asStr();
                            final var i = arguments[0].asNum();
                            return ChrValue.create(str.get().charAt((int) i.get()));
                        }));

        env.register("ifstream", null,
                new Func(new String[] { Types.STR }, new String[] { "filename" },
                        (environment, object, arguments) -> {
                            final var filename = arguments[0].asStr();
                            return IFStream.create(filename.get());
                        }));
        env.register("readLine", "ifstream",
                new Func(new String[0], new String[0],
                        (environment, object, arguments) -> {
                            final var stream = (IFStream) object;
                            return stream.readLine();
                        }));

        Interpreter.evaluate(env, program);

        System.out.println(
                env.execute("main", null, NumValue.create(args.length - 1),
                        StrValue.createArray(args, 1, args.length - 1)));
    }
}
