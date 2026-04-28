package com.blackbox.plog.pLogs.redaction

import androidx.annotation.Keep

/**
 * Controls automatic PII scrubbing applied to every log string before it is written to disk
 * or published via MQTT.  Set via [com.blackbox.plog.pLogs.config.LogsConfig.redactionConfig].
 *
 * Both mechanisms are independent and cumulative:
 *  - [enableBuiltInPatterns] — opt-in regex patterns for common PII types.
 *  - [customRules]           — arbitrary regex rules defined by the caller.
 *
 * For field-level masking of structured objects, annotate fields with [@Sensitive][Sensitive]
 * and pass the object to [com.blackbox.plog.pLogs.PLog.redact] or the object-accepting
 * [com.blackbox.plog.pLogs.PLog.logThis] overload.
 *
 * Example:
 *   RedactionConfig(
 *       enableBuiltInPatterns = setOf(BuiltInPattern.PHONE_NUMBER, BuiltInPattern.EMAIL),
 *       customRules = listOf(
 *           RedactionRule("SSN", """\b\d{3}-\d{2}-\d{4}\b""", MaskType.HASH),
 *           RedactionRule("InternalKey", "sk_live_[A-Za-z0-9]+", MaskType.PARTIAL)
 *       ),
 *       defaultMaskType = MaskType.FULL_MASK
 *   )
 */
@Keep
data class RedactionConfig(
    val enableBuiltInPatterns: Set<BuiltInPattern> = emptySet(),
    val customRules: List<RedactionRule> = emptyList(),
    val defaultMaskType: MaskType = MaskType.FULL_MASK
)