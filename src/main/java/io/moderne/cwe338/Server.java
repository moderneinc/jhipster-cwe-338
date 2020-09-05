package io.moderne.cwe338;

import io.javalin.Javalin;
import kotlin.text.Charsets;
import org.openrewrite.Change;
import org.openrewrite.Refactor;
import org.openrewrite.java.JavaParser;

public class Server {
    private static final JavaParser parser = JavaParser.fromJavaVersion()
            .classpath(JavaParser.dependenciesFromClasspath("commons-lang3"))
            .build();

    private static final Refactor refactor = new Refactor()
            .visit(new CWE338());

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.post("/fix", ctx -> {
            Change change = refactor.fix(parser.parse(new String(ctx.bodyAsBytes(), Charsets.UTF_8)))
                    .iterator()
                    .next();
            ctx.result(change.getFixed().printTrimmed());
        });
    }
}
