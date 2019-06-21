package com.blackbox.plog.pLogs

import android.os.Environment
import com.blackbox.plog.BuildConfig
import com.blackbox.plog.pLogs.config.LogsConfig
import com.blackbox.plog.pLogs.impl.PLogImpl
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowEnvironment


/**
 * Created by umair on 2019-05-15 12:06
 * for ULog
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class PLogTest {

    @Before
    fun init() {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED)
    }

    @Test
    fun checkLogsConfigurationIsValid() {

        //Mockito.doReturn(Environment.MEDIA_MOUNTED).`when`(Environment.getExternalStorageState())

        Mockito.doReturn(LogsConfig()).`when`(PLogImpl.getConfig())

        assertNotNull(PLogImpl.getConfig())

    }
}