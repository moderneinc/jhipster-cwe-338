package io.moderne.cwe338;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.openrewrite.Refactor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class CloudFunction implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        JavaParser parser = JavaParser.fromJavaVersion()
                .classpath(JavaParser.dependenciesFromClasspath(
                        "commons-lang3", "commons-lang"))
                .build();

        Refactor refactor = new Refactor()
                .visit(new CWE338());

        String input = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        List<J.CompilationUnit> parse = parser.reset().parse(input);

        String responseBody = refactor.fix(Collections.singletonList(parse.iterator().next()))
                .stream()
                .findAny()
                .map(change -> change.getFixed().print())
                .orElse(parse.iterator().next().print());

        J.CompilationUnit fixed = refactor.fixed(parse.iterator().next());

        assert fixed != null;
        response.getWriter().write(responseBody);
    }
}
