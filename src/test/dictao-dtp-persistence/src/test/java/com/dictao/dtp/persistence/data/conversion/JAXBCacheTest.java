package com.dictao.dtp.persistence.data.conversion;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class JAXBCacheTest {
    
    public JAXBCacheTest() {
    }

    /**
     * Test to ensure xsd file in reachable and have not been moved or deleted.
     */
    @Test
    public void testSchemaInit() throws Exception {
        assertNotNull(JAXBCache.getSchemaV1());
        assertNotNull(JAXBCache.getContextV1());
    }
}
