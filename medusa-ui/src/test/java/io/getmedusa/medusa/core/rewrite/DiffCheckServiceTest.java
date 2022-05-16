package io.getmedusa.medusa.core.rewrite;

import io.getmedusa.medusa.core.injector.DiffCheckService;
import org.junit.jupiter.api.Test;

class DiffCheckServiceTest {

    private final DiffCheckService diffCheckService = new DiffCheckService();

    @Test
    void testAdditions() {
        diffCheckService.diffCheckDocuments(null, null);
    }

    @Test
    void testRemoval() {

    }

    @Test
    void testEdit() {

    }

}
