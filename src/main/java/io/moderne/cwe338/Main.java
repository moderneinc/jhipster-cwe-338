package io.moderne.cwe338;

import org.openrewrite.Refactor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static final JavaParser parser = JavaParser.fromJavaVersion()
            .classpath(JavaParser.dependenciesFromClasspath("commons-lang3"))
            .build();

    private static final Refactor refactor = new Refactor()
            .visit(new CWE338());

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Requires a single argument, the path to a source file to be fixed");
        }

        File file = new File(args[0]);
        if (!file.getName().endsWith(".java") || !file.exists()) {
            throw new IllegalArgumentException("Requires a single argument, the path to a source file to be fixed");
        }

        J.CompilationUnit fixed = refactor.fixed(parser.parse(file.toPath(), null));
        assert fixed != null;
        Files.writeString(file.toPath(), fixed.print());
    }
}
