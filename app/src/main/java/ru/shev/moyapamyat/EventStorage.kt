package ru.shev.moyapamyat

import java.util.Base64

/** Persistent, UI-independent representation of a calendar event. */
data class StoredEvent(
    val day: Int,
    val title: String,
    val colorArgb: Int,
    val recurringMonthly: Boolean = false
)

/**
 * Small versioned text format for SharedPreferences.
 * Titles are Base64 URL-safe encoded, so Russian text and separators are safe.
 */
object EventStorage {
    private const val VERSION = "v1"

    fun encode(events: List<StoredEvent>): String = buildString {
        append(VERSION)
        events.forEach { event ->
            append('\n')
            append(event.day).append('|')
            append(event.colorArgb).append('|')
            append(if (event.recurringMonthly) "1" else "0").append('|')
            append(Base64.getUrlEncoder().withoutPadding()
                .encodeToString(event.title.toByteArray(Charsets.UTF_8)))
        }
    }

    fun decode(raw: String?): List<StoredEvent> {
        if (raw.isNullOrBlank()) return emptyList()
        val lines = raw.lineSequence().toList()
        if (lines.firstOrNull() != VERSION) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            runCatching {
                val parts = line.split('|')
                if (parts.size != 4) return@runCatching null
                val day = parts[0].toInt()
                val color = parts[1].toInt()
                val recurring = when (parts[2]) {
                    "0" -> false
                    "1" -> true
                    else -> return@runCatching null
                }
                val title = String(Base64.getUrlDecoder().decode(parts[3]), Charsets.UTF_8)
                if (day !in 1..31 || title.isBlank()) return@runCatching null
                StoredEvent(day, title, color, recurring)
            }.getOrNull()
        }
    }
}
