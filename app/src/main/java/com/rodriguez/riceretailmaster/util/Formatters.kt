package com.rodriguez.riceretailmaster.util

import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
object Formatters {

    private val timeFmt = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
    private val dateLongFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US)

    private fun parse(iso: String?): OffsetDateTime? {
        if (iso.isNullOrBlank()) return null
        return runCatching { OffsetDateTime.parse(iso) }
            .recoverCatching { OffsetDateTime.ofInstant(Instant.parse(iso), ZoneId.systemDefault()) }
            .getOrNull()
    }

    fun time(iso: String?): String =
        parse(iso)?.atZoneSameInstant(ZoneId.systemDefault())?.format(timeFmt) ?: "--:--"

    fun localDate(iso: String?): LocalDate? =
        parse(iso)?.atZoneSameInstant(ZoneId.systemDefault())?.toLocalDate()

    fun dateLong(date: LocalDate): String = date.format(dateLongFmt)

    fun relative(iso: String?): String {
        val odt = parse(iso) ?: return ""
        val seconds = java.time.Duration.between(odt.toInstant(), Instant.now()).seconds
        return when {
            seconds < 60 -> "Now"
            seconds < 3600 -> "${seconds / 60}m ago"
            seconds < 86_400 -> "${seconds / 3600}h ago"
            else -> "${seconds / 86_400}d ago"
        }
    }
}
