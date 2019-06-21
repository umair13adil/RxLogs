package com.blackbox.plog.pLogs.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;

import javax.crypto.SecretKey;

import kotlin.Pair;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by umair on 2019-05-15 13:57
 * for ULog
 */
public class LogWriterTest {

    @InjectMocks
    LogWriter logWriter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSecretKey() throws Exception {
        SecretKey result = logWriter.getSecretKey();
        assertEquals(null, result);
    }

    @Test
    public void testSetSecretKey() throws Exception {
        logWriter.setSecretKey(null);
    }

    @Test
    public void testWriteEncryptedLogs() throws Exception {
        logWriter.writeEncryptedLogs("logFormatted");
    }

    @Test
    public void testWriteSimpleLogs() throws Exception {
        logWriter.writeSimpleLogs("logFormatted");
    }

    @Test
    public void testShouldWriteLog() throws Exception {
        Pair<Boolean, String> result = logWriter.shouldWriteLog(new File(getClass().getResource("/com/blackbox/plog/pLogs/impl/PleaseReplaceMeWithTestFile.txt").getFile()), true, "logFileName");
        Assert.assertEquals(null, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme