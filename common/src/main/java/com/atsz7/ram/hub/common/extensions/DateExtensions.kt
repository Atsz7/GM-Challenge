package com.atsz7.ram.hub.common.extensions

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private const val API_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val DISPLAY_DATE_PATTERN = "MMM d, yyyy"

/**
 * Parses an ISO-8601 UTC timestamp (e.g. "2017-11-05T11:53:44.737Z") as returned by the
 * Rick and Morty API into a human-readable date (e.g. "Nov 5, 2017").
 * @return [String] with the new human-readable format.
 */
fun String.toFormattedDate(): String {
    val parser = SimpleDateFormat(API_DATE_PATTERN, Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val date = runCatching { parser.parse(this) }.getOrNull() ?: return this
    return SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.US).format(date)
}
