package io.moderne.cwe338;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.openrewrite.Refactor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CloudFunction implements HttpFunction {
    JavaParser parser = JavaParser.fromJavaVersion()
            .classpath(JavaParser.dependenciesFromClasspath("commons-lang3"))
            .build();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Refactor refactor = new Refactor()
                .visit(new CWE338());

        String input = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        List<J.CompilationUnit> parse = parser.reset().parse(input);
        J.CompilationUnit fixed = refactor.fixed(parse.iterator().next());

        assert fixed != null;
        response.getWriter().write(fixed.print());
    }
}
