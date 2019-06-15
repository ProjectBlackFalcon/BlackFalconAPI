package com.ankamagames.dofus.core.network;

import org.junit.Test;

public class DofusConnectorTest {

    @Test
    public void testConnect(){
        DofusConnector connector = new DofusConnector(null);
        connector.run();
    }
}
