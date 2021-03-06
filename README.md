## Patching JHipster CWE-338

At one point, JHipster generated code using a Pseudo-Random Number Generator (PRNG) meant for use in a security context, but the PRNG's algorithm is not cryptographically strong ([CWE-338](https://cwe.mitre.org/data/definitions/338.html)).

This Rewrite visitor patches these generated files, using a Cryptographically Secure Pseudo-Random Number Generator (CSPRNG).

## Use

Deploy to Google Cloud Function using `deploy.sh`. The Google Cloud CLI will respond with a URL of the running function. Send an HTTP POST request whose body contains a source file to be patched and receive the patched source file in the response body.

## Example

This is the vulnerable code before patching:

```java
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
```

This is the fixed code after patching:

```java
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
```
