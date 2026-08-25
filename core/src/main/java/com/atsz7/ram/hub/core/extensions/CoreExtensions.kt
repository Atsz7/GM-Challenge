package com.atsz7.ram.hub.core.extensions

import java.text.Normalizer

private val diacriticsRegex = Regex("\\p{Mn}+")
private val nonAlphanumericRegex = Regex("[^\\p{L}\\p{Nd}]+")

/**
 * Lowercases and strips diacritics (Example: "Rick Sánchez" -> "rick sanchez") so it
 * can be stored alongside the raw value and matched regardless of case or accents.
 * @return [String].
 */
fun String.normalizeForSearch(): String {
    val normalized = Normalizer.normalize(this.trim(), Normalizer.Form.NFD)
    return diacriticsRegex.replace(normalized, "").lowercase()
}

/**
 * Normalizes the [String] input and collapses it into a single space-separated term.
 * Example: "  Rick  Sán!" -> "rick san".
 * @return [String].
 */
fun String.toNormalizedSearchTerm(): String =
    normalizeForSearch()
        .split(nonAlphanumericRegex)
        .filter { it.isNotBlank() }
        .joinToString(separator = " ")
