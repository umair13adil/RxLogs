package com.blackbox.plog.elk

import com.blackbox.plog.elk.models.fields.MetaInfo

object PLogMetaInfoProvider {

    var elkStackSupported: Boolean = false
    internal var metaInfo = MetaInfo()

    fun setMetaInfo(metaInfo: MetaInfo) {
        this.metaInfo = metaInfo
    }
}