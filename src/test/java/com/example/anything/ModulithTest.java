package com.example.anything;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithTest {

    @Test
    void verifyModules() {
        ApplicationModules modules = ApplicationModules.of(AnythingApplication.class);

        modules.verify();

        System.out.println(modules);
    }
}