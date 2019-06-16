package com.ankamagames.dofus.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class FilesUtilsTest {

    @Test
    public void testParseNameId() throws IOException {
        Assert.assertNotNull(FilesUtils.parseMessageNameId());
    }
}
