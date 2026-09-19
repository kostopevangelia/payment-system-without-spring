package com.evangeliakostop.paymentsystem.utils;

import com.evangeliakostop.paymentsystem.common.utils.UniqueIdGenerator;
import org.junit.jupiter.api.Test;

class UniqueIdGeneratorTest {

    @Test
    void generateSecureToken() {
        String token = UniqueIdGenerator.generateSecureToken();
        System.out.println(token);
    }
}