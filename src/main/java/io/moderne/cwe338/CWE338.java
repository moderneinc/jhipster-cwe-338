package io.moderne.cwe338;

import org.openrewrite.Tree;
import org.openrewrite.java.AddField;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaRefactorVisitor;
import org.openrewrite.java.ShiftFormatRightVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TreeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.openrewrite.Formatting.EMPTY;
import static org.openrewrite.Formatting.format;

public class CWE338 extends JavaRefactorVisitor {

    public CWE338() {
        setCursoringOn();
    }

    @Override
    public boolean isIdempotent() {
        return false;
    }

    @Override
    public J visitClassDecl(J.ClassDecl classDecl) {
        J.CompilationUnit cu = getCursor().firstEnclosing(J.CompilationUnit.class);
        if (cu != null && cu.getPackageDecl() != null &&
                classDecl.getSimpleName().equals("RandomUtil")) {
            andThen(new AddField.Scoped(classDecl,
                    Arrays.asList(
                            new J.Modifier.Private(Tree.randomId(), EMPTY),
                            new J.Modifier.Static(Tree.randomId(), format(" ")),
                            new J.Modifier.Final(Tree.randomId(), format(" "))
                    ),
                    "java.security.SecureRandom",
                    "SECURE_RANDOM",
                    "new SecureRandom()"));

            andThen(new FixCWE338());
        }
        return super.visitClassDecl(classDecl);
    }

    private static class FixCWE338 extends JavaRefactorVisitor {
        private JavaParser parser;

        @Override
        public J visitCompilationUnit(J.CompilationUnit cu) {
            parser = JavaParser.fromJavaVersion()
                    .classpath(JavaParser.dependenciesFromClasspath("commons-lang3", "commons-lang"))
                    .build();
            return super.visitCompilationUnit(cu);
        }

        @Override
        public J visitClassDecl(J.ClassDecl classDecl) {
            J.ClassDecl c = refactor(classDecl, super::visitClassDecl);
            List<J> statements = new ArrayList<>(c.getBody().getStatements());

            J staticInit = TreeBuilder.buildDeclaration(
                    parser,
                    classDecl,
                    "static {\n" +
                            "    SECURE_RANDOM.nextBytes(new byte[64]);\n" +
                            "}"
            );

            for (int i = 0; i < statements.size(); i++) {
                if (!(statements.get(i) instanceof J.VariableDecls)) {
                    statements.add(i, staticInit);
                    break;
                }
            }

            J generateRandomAlphanumericString = TreeBuilder.buildDeclaration(
                    parser,
                    classDecl,
                    "private static String generateRandomAlphanumericString() {\n" +
                            "    return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM);\n" +
                            "}"
            );

            for (int i = 0; i < statements.size(); i++) {
                if (statements.get(i) instanceof J.MethodDecl && ((J.MethodDecl) statements.get(i)).isConstructor()) {
                    statements.add(i + 1, generateRandomAlphanumericString);
                    break;
                }
            }

            c = c.withBody(c.getBody().withStatements(statements));

            andThen(new ShiftFormatRightVisitor(staticInit, 4, true));
            andThen(new ShiftFormatRightVisitor(generateRandomAlphanumericString, 4, true));

            return c;
        }

        @Override
        public J visitMethodInvocation(J.MethodInvocation method) {
            J.MethodInvocation m = refactor(method, super::visitMethodInvocation);

            m = m.withSelect(null);
            m = m.withName(m.getName().withName("generateRandomAlphanumericString"));
            m = m.withArgs(new J.MethodInvocation.Arguments(m.getArgs().getId(), emptyList(), EMPTY));

            return m;
        }
    }
}
