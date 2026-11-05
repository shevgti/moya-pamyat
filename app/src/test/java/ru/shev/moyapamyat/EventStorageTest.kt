package ru.shev.moyapamyat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventStorageTest {
    @Test fun roundTripsRussianTitlesAndSpecialCharacters() {
        val events = listOf(
            StoredEvent(12, "Оплатить кредит | ноябрь", 0xFFFF9E16.toInt(), true),
            StoredEvent(4, "День рождения Марины 🎂", 0xFFB56CFF.toInt(), false)
        )
        assertEquals(events, EventStorage.decode(EventStorage.encode(events)))
    }

    @Test fun emptyEventListRoundTrips() {
        assertEquals(emptyList<StoredEvent>(), EventStorage.decode(EventStorage.encode(emptyList())))
    }

    @Test fun unknownVersionIsIgnored() {
        assertTrue(EventStorage.decode("v2\n12|123|1|VGVzdA").isEmpty())
    }

    @Test fun malformedAndOutOfRangeRecordsAreIgnored() {
        val raw = "v1\n0|123|1|VGVzdA\n32|123|1|VGVzdA\n12|123|x|VGVzdA\n12|123|1|VGVzdA"
        assertEquals(listOf(StoredEvent(12, "Test", 123, true)), EventStorage.decode(raw))
    }
}
