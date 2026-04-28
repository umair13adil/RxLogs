package com.blackbox.plog.pLogs.redaction

import androidx.annotation.Keep

/**
 * A custom regex-based redaction rule.
 *
 * Every log string is scanned for [patternString] after built-in patterns are applied.
 * Matches are replaced by [replacement] (when set) or by applying [maskType] to the matched text.
 *
 * @param name          Human-readable label for diagnostics.
 * @param patternString A valid Java regex pattern string.
 * @param maskType      How matched text is masked (ignored when [replacement] is non-null).
 * @param replacement   Literal replacement string; overrides [maskType] when provided.
 *
 * Example — mask US Social Security Numbers with their SHA-256 fingerprint:
 *   RedactionRule("SSN", """\b\d{3}-\d{2}-\d{4}\b""", MaskType.HASH)
 *
 * Example — replace all occurrences of an internal codename with a fixed label:
 *   RedactionRule("InternalCodename", "ProjectNebula", replacement = "[REDACTED]")
 */
@Keep
data class RedactionRule(
    val name: String,
    val patternString: String,
    val maskType: MaskType = MaskType.FULL_MASK,
    val replacement: String? = null
)