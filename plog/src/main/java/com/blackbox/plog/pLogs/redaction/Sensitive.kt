package com.blackbox.plog.pLogs.redaction

import androidx.annotation.Keep

/**
 * Marks a field as sensitive. When an object is serialized via [com.blackbox.plog.pLogs.PLog.redact]
 * or logged via the object-accepting [com.blackbox.plog.pLogs.PLog.logThis] overload, the field
 * value is replaced according to [maskType] instead of being written as plain text.
 *
 * In a data class constructor, use the field-site target:
 *   data class Customer(@field:Sensitive val phone: String, val orderId: String)
 *
 * In a regular class body, no extra target is needed:
 *   class Session {
 *       @Sensitive(maskType = MaskType.HASH) var authToken: String = ""
 *   }
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Keep
annotation class Sensitive(val maskType: MaskType = MaskType.FULL_MASK)

/**
 * Strategy used to replace a sensitive value.
 *
 * FULL_MASK  → "***"
 * HASH       → "[sha256:ab3f1c2d]"  (first 8 hex chars of SHA-256 of the original value)
 * PARTIAL    → "Jo******ne"         (first 2 + asterisks + last 2 chars; useful for debugging)
 */
@Keep
enum class MaskType { FULL_MASK, HASH, PARTIAL }