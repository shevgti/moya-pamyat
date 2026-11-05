package ru.shev.moyapamyat

import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRulesTest {
    @Test fun november2026StartsOnSundayAndUsesFiveWeeks() {
        val month = YearMonth.of(2026, 11)
        assertEquals(6, CalendarRules.firstDayOffset(month))
        assertEquals(35, CalendarRules.cellCount(month))
        assertEquals(1, CalendarRules.dayAtCell(month, 6))
        assertEquals(30, CalendarRules.dayAtCell(month, 35 - 1))
    }

    @Test fun february2024IncludesLeapDay() {
        val month = YearMonth.of(2024, 2)
        assertEquals(29, month.lengthOfMonth())
        assertEquals(29, CalendarRules.dayAtCell(month, CalendarRules.cellCount(month) - 1))
        assertTrue(CalendarRules.isValidDay(month, 29))
        assertFalse(CalendarRules.isValidDay(month, 30))
    }

    @Test fun february2026HasTwentyEightDaysAndFourWeeks() {
        val month = YearMonth.of(2026, 2)
        assertEquals(28, month.lengthOfMonth())
        assertEquals(28, CalendarRules.cellCount(month))
        assertEquals(28, CalendarRules.dayAtCell(month, 27))
    }

    @Test fun cellsOutsideTheMonthAreEmpty() {
        val month = YearMonth.of(2026, 11)
        assertNull(CalendarRules.dayAtCell(month, 0))
        assertNull(CalendarRules.dayAtCell(month, 34 + 1))
        assertFalse(CalendarRules.isValidDay(month, 0))
        assertFalse(CalendarRules.isValidDay(month, 31))
    }

    @Test fun gridAlwaysContainsWholeWeeks() {
        for (year in 2024..2032) {
            for (monthNumber in 1..12) {
                val month = YearMonth.of(year, monthNumber)
                val count = CalendarRules.cellCount(month)
                assertTrue(count % 7 == 0)
                assertTrue(count in 28..42)
                val days = (0 until count).mapNotNull { CalendarRules.dayAtCell(month, it) }
                assertEquals((1..month.lengthOfMonth()).toList(), days)
            }
        }
    }
}
