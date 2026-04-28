package com.blackbox.plog.pLogs.redaction

import androidx.annotation.Keep

/**
 * Ready-made PII patterns. Opt in per-pattern via [RedactionConfig.enableBuiltInPatterns].
 * Matching is applied to every log string before it is written to disk or published via MQTT.
 */
@Keep
enum class BuiltInPattern(private val regex: Regex) {

    /** Matches US/international phone numbers: 555-1234, (555) 867-5309, +1 555 867 5309 */
    PHONE_NUMBER(
        Regex("""(\+?1[\s\-.]?)?(\(?\d{3}\)?[\s\-.]?\d{3}[\s\-.]?\d{4})""")
    ),

    /** Matches standard e-mail addresses. */
    EMAIL(
        Regex("""[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""")
    ),

    /** Matches 16-digit card numbers with optional spaces or dashes between groups. */
    CREDIT_CARD(
        Regex("""\b\d{4}[\s\-]?\d{4}[\s\-]?\d{4}[\s\-]?\d{4}\b""")
    ),

    /** Matches JWT bearer tokens (three Base64url segments starting with eyJ). */
    JWT_TOKEN(
        Regex("""eyJ[A-Za-z0-9\-_=]+\.eyJ[A-Za-z0-9\-_=]+\.[A-Za-z0-9\-_.+/=]*""")
    ),

    /** Matches dotted-decimal IPv4 addresses. */
    IP_ADDRESS(
        Regex("""\b(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\b""")
    );

    internal fun apply(text: String, maskType: MaskType): String =
        regex.replace(text) { Redactor.applyMask(it.value, maskType) }
}