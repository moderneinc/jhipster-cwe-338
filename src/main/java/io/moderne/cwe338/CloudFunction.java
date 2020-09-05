package io.moderne.cwe338;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.openrewrite.Change;
import org.openrewrite.Parser;
import org.openrewrite.Refactor;
import org.openrewrite.java.JavaParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.Collections;

public class CloudFunction implements HttpFunction {
    private static final JavaParser parser = JavaParser.fromJavaVersion()
            .classpath(JavaParser.dependenciesFromClasspath("commons-lang3"))
            .build();

    private static final Refactor refactor = new Refactor()
            .visit(new CWE338());

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Parser.Input input = new Parser.Input(
                Paths.get("RandomUtils.java"),
                () -> {
                    try {
                        return request.getInputStream();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
        );

        Change change = refactor.fix(parser.parseInputs(Collections.singleton(input), null))
                .iterator()
                .next();

        response.getWriter().write(change.getFixed().printTrimmed());
    }
}
