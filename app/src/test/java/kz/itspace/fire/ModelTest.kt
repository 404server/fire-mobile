package kz.itspace.fire

import kz.itspace.fire.model.Alarm
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.Incident
import kz.itspace.fire.model.busyUnits
import kz.itspace.fire.model.formatElapsed
import kz.itspace.fire.model.queue
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelTest {

    private fun alarm(id: Int, status: CallStatus, createdAt: Long, unit: String? = null) =
        Alarm(id, Incident.Fire(1), "пр. Абая, $id", createdAt = createdAt, status = status, unit = unit)

    private val alarms = listOf(
        alarm(1, CallStatus.EN_ROUTE, createdAt = 0, unit = "Пожарная-1"),
        alarm(2, CallStatus.NEW, createdAt = 200),
        alarm(3, CallStatus.NEW, createdAt = 100),
        alarm(4, CallStatus.DONE, createdAt = 50, unit = "Пожарная-2"),
    )

    @Test
    fun queueShowsOldestWaitingFirstAndHidesDone() {
        assertEquals(listOf(3, 2, 1), alarms.queue("", showDone = false).map { it.id })
        assertEquals(listOf(3, 2, 1, 4), alarms.queue("", showDone = true).map { it.id })
        assertEquals(listOf(2), alarms.queue("абая, 2", showDone = true).map { it.id })
    }

    @Test
    fun onlyUnitsOnActiveCallsAreBusy() {
        assertEquals(setOf("Пожарная-1"), alarms.busyUnits())
    }

    @Test
    fun elapsedFormat() {
        assertEquals("0:05", formatElapsed(5_000))
        assertEquals("12:34", formatElapsed(754_000))
        assertEquals("1:02:03", formatElapsed(3_723_000))
    }
}
