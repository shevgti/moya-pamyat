package ru.shev.moyapamyat

import java.time.YearMonth

/**
 * Pure calendar-grid rules shared by the UI and local unit tests.
 * Monday is column 0 and Sunday is column 6.
 */
object CalendarRules {
    fun firstDayOffset(month: YearMonth): Int =
        month.atDay(1).dayOfWeek.value - 1

    fun cellCount(month: YearMonth): Int {
        val occupied = firstDayOffset(month) + month.lengthOfMonth()
        return ((occupied + 6) / 7) * 7
    }

    fun dayAtCell(month: YearMonth, cellIndex: Int): Int? {
        if (cellIndex !in 0 until cellCount(month)) return null
        val day = cellIndex - firstDayOffset(month) + 1
        return day.takeIf { it in 1..month.lengthOfMonth() }
    }

    fun isValidDay(month: YearMonth, day: Int): Boolean =
        day in 1..month.lengthOfMonth()
}
