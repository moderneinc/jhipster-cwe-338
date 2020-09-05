package io.moderne.cwe338;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.apache.commons.io.Charsets;
import org.openrewrite.Refactor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;

public class CloudFunction implements HttpFunction {
    private static final JavaParser parser = JavaParser.fromJavaVersion()
            .classpath(JavaParser.dependenciesFromClasspath("commons-lang3"))
            .build();

    private static final Refactor refactor = new Refactor()
            .visit(new CWE338());

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        J.CompilationUnit fixed = refactor.fixed(parser.reset()
                .parse(new String(request.getInputStream().readAllBytes(), Charsets.UTF_8))
                .iterator()
                .next());

        assert fixed != null;
        response.getWriter().write(fixed.printTrimmed());
    }
}
