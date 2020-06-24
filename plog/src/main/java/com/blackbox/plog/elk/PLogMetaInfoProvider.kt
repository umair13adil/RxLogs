package com.blackbox.plog.elk

object PLogMetaInfoProvider {

    var elkStackSupported: Boolean = false
    internal var metaInfo = MetaInfo()

    fun setMetaInfo(metaInfo: MetaInfo) {
        this.metaInfo = metaInfo
    }
}