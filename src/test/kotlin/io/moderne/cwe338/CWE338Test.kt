package io.moderne.cwe338

import org.junit.jupiter.api.Test
import org.openrewrite.RefactorVisitor
import org.openrewrite.RefactorVisitorTestForParser
import org.openrewrite.java.JavaParser
import org.openrewrite.java.tree.J

class CWE338Test : RefactorVisitorTestForParser<J.CompilationUnit> {
    override val parser: JavaParser = JavaParser.fromJavaVersion()
            .classpath(JavaParser.dependenciesFromClasspath("commons-lang3", "commons-lang"))
            .build()

    override val visitors: Iterable<RefactorVisitor<*>> = listOf(CWE338())

    @Test
    fun cwe338CommonsLang2() = assertRefactored(
            before = """
                package au.com.suncorp.easyshare.services.util;

                import org.apache.commons.lang.RandomStringUtils;

                public final class RandomUtil {
                    private RandomUtil() {
                    }

                    public static String generateString(int count) {
                        return RandomStringUtils.randomAlphanumeric(count);
                    }
                }
            """,
            after = """
                package au.com.suncorp.easyshare.services.util;
                
                import org.apache.commons.lang.RandomStringUtils;
                
                import java.security.SecureRandom;
                
                public final class RandomUtil {
                    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
                
                    static {
                        SECURE_RANDOM.nextBytes(new byte[64]);
                    }
                    private RandomUtil() {
                    }
                
                    private static String generateRandomAlphanumericString() {
                        return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM);
                    }
                
                    public static String generateString(int count) {
                        return generateRandomAlphanumericString();
                    }
                }  
            """
    )

    @Test
    fun cwe338() = assertRefactored(
            before = """
                package io.moderne.service.util;
                
                import org.apache.commons.lang3.RandomStringUtils;
    
                public class RandomUtil {
                    private static final int DEF_COUNT = 20;
    
                    private RandomUtil() {
                    }
    
                    public static String generatePassword() {
                        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
                    }
    
                    public static String generateActivationKey() {
                        return RandomStringUtils.randomNumeric(DEF_COUNT);
                    }
    
                    public static String generateResetKey() {
                        return RandomStringUtils.randomNumeric(DEF_COUNT);
                    }
                
                    public static String generateSeriesData() {
                        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
                    }
                
                    public static String generateTokenData() {
                        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
                    }
                }
            """,
            after = """
                package io.moderne.service.util;

                import org.apache.commons.lang3.RandomStringUtils;
                
                import java.security.SecureRandom;
                
                public class RandomUtil {
                    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
                    private static final int DEF_COUNT = 20;
                
                    static {
                        SECURE_RANDOM.nextBytes(new byte[64]);
                    }
                
                    private RandomUtil() {
                    }
                
                    private static String generateRandomAlphanumericString() {
                        return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM);
                    }
                
                    public static String generatePassword() {
                        return generateRandomAlphanumericString();
                    }
                
                    public static String generateActivationKey() {
                        return generateRandomAlphanumericString();
                    }
                
                    public static String generateResetKey() {
                        return generateRandomAlphanumericString();
                    }
                
                    public static String generateSeriesData() {
                        return generateRandomAlphanumericString();
                    }
                
                    public static String generateTokenData() {
                        return generateRandomAlphanumericString();
                    }
                } 
            """
    )
}
