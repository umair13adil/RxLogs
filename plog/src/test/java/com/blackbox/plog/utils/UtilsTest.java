package com.blackbox.plog.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Created by umair on 2019-05-15 15:11
 * for ULog
 */
public class UtilsTest {

    @InjectMocks
    PLogUtils utils;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDirIfNotExists() throws Exception {
        boolean result = utils.createDirIfNotExists("path");
        Assert.assertEquals(true, result);
    }

    @Test
    public void testGetStackTrace() throws Exception {
        String result = utils.getStackTrace(null);
        Assert.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    public void testGetStackTrace2() throws Exception {
        String result = utils.getStackTrace(null);
        Assert.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    public void testBytesToReadable() throws Exception {
        String result = utils.bytesToReadable(0);
        Assert.assertEquals("replaceMeWithExpectedResult", result);
    }

    @Test
    public void testReadAssetsXML() throws Exception {
        String result = utils.readAssetsXML("fileName", null);
        Assert.assertEquals("replaceMeWithExpectedResult", result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme