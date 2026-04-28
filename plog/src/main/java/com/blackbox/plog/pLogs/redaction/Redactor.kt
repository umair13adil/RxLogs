package com.blackbox.plog.pLogs.redaction

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.security.MessageDigest

/**
 * The redaction engine. Called by PLogImpl after a log string is formatted and before it is
 * handed to the writer. Also invoked explicitly via PLog.redact(obj) for structured objects.
 *
 * Thread-safety: [configure] replaces immutable state atomically. [applyToString] and
 * [redactObject] are read-only after configuration and safe to call from any thread.
 */
internal object Redactor {

    @Volatile private var config: RedactionConfig? = null

    // Compiled once at configure() time; list reference is replaced atomically.
    @Volatile private var compiledCustomRules: List<Pair<Regex, RedactionRule>> = emptyList()

    fun configure(cfg: RedactionConfig?) {
        config = cfg
        compiledCustomRules = cfg?.customRules?.mapNotNull { rule ->
            runCatching { Regex(rule.patternString) to rule }.getOrNull()
        } ?: emptyList()
    }

    /**
     * Runs all enabled built-in patterns then all custom rules against [text].
     * Returns [text] unchanged when no [RedactionConfig] is set.
     */
    fun applyToString(text: String): String {
        val cfg = config ?: return text
        var result = text
        for (pattern in cfg.enableBuiltInPatterns) {
            result = pattern.apply(result, cfg.defaultMaskType)
        }
        for ((regex, rule) in compiledCustomRules) {
            result = regex.replace(result) { match ->
                rule.replacement ?: applyMask(match.value, rule.maskType)
            }
        }
        return result
    }

    /**
     * Serializes [obj] into a log-safe string.
     *
     * Fields annotated with [@Sensitive][Sensitive] are replaced by their masked form.
     * Non-annotated primitive/string fields are written as-is. Non-annotated complex fields
     * are recursed up to [depth] 3 to catch nested annotated types. The final string is then
     * passed through [applyToString] so regex rules also apply.
     *
     * Falls back to [obj].toString() + [applyToString] when no [@Sensitive][Sensitive]
     * annotation is found anywhere in the object graph.
     */
    fun redactObject(obj: Any, depth: Int = 0): String {
        if (depth > 3) return applyToString(obj.toString())

        val clazz = obj.javaClass

        // Scalars — just apply string rules
        if (clazz.isPrimitive || obj is String || obj is Number || obj is Boolean || obj is Char) {
            return applyToString(obj.toString())
        }

        val fields = collectFields(clazz)

        if (fields.none { it.getDeclaredAnnotation(Sensitive::class.java) != null }) {
            // No @Sensitive fields anywhere in this object — toString + regex
            return applyToString(obj.toString())
        }

        val sb = StringBuilder(clazz.simpleName).append("{")
        var first = true
        for (field in fields) {
            val value = try {
                field.isAccessible = true
                field.get(obj)
            } catch (_: Exception) {
                continue
            }

            if (!first) sb.append(", ")
            first = false
            sb.append(field.name).append("=")

            val annotation = field.getDeclaredAnnotation(Sensitive::class.java)
            when {
                annotation != null ->
                    sb.append(applyMask(value?.toString() ?: "null", annotation.maskType))

                value != null && depth < 3
                        && !value.javaClass.isPrimitive
                        && value !is String
                        && value !is Number
                        && value !is Boolean ->
                    sb.append(redactObject(value, depth + 1))

                else -> sb.append(value?.toString() ?: "null")
            }
        }
        sb.append("}")
        return applyToString(sb.toString())
    }

    fun applyMask(value: String, maskType: MaskType): String = when (maskType) {
        MaskType.FULL_MASK -> "***"
        MaskType.HASH      -> "[sha256:${sha256(value).take(8)}]"
        MaskType.PARTIAL   -> when {
            value.length <= 3 -> "***"
            value.length <= 6 -> "${value.first()}***${value.last()}"
            else              -> "${value.take(2)}${"*".repeat(minOf(value.length - 4, 8))}${value.takeLast(2)}"
        }
    }

    // Walks the class hierarchy collecting instance fields, skipping synthetic and static ones.
    private fun collectFields(clazz: Class<*>): List<Field> {
        val result = mutableListOf<Field>()
        var c: Class<*>? = clazz
        while (c != null && c != Any::class.java) {
            c.declaredFields
                .filter { !it.isSynthetic && !Modifier.isStatic(it.modifiers) }
                .forEach { result.add(it) }
            c = c.superclass
        }
        return result
    }

    private fun sha256(input: String): String = try {
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    } catch (_: Exception) {
        input.hashCode().toString(16).padStart(8, '0')
    }
}