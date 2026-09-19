package com.evangeliakostop.paymentsystem.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UniqueIdGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateSecureToken() {
        // Generate 256 bits (32 bytes) of secure random data
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // Hash the random data with SHA-56 to get a 256-bit hash
        String fullToken = DigestUtils.sha256Hex(randomBytes);
        fullToken = "txn" + fullToken;

        // Take only the first 160 bits (40 hex characters)
        return fullToken.substring(0, 40);
    }
}
