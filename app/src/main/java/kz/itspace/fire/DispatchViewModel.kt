package kz.itspace.fire

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kz.itspace.fire.model.Alarm
import kz.itspace.fire.model.Ambulance
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.EntityRandomizer
import kz.itspace.fire.model.FireTruck
import kz.itspace.fire.model.Incident
import kz.itspace.fire.model.Vehicle

class DispatchViewModel : ViewModel() {

    val units: List<Vehicle> = listOf(
        FireTruck("Пожарная-1"),
        FireTruck("Пожарная-2"),
        Ambulance("Скорая-1"),
        Ambulance("Скорая-2"),
    )

    val alarms = mutableStateListOf<Alarm>()
    var message by mutableStateOf("Смена началась")
        private set
    var counter by mutableIntStateOf(0)
        private set

    private var nextId = 1

    init {
        repeat(3) { addAlarm(EntityRandomizer.randomAddress(), EntityRandomizer.randomIncident()) }
    }

    fun addAlarm(address: String, incident: Incident) {
        alarms.add(0, Alarm(nextId++, incident, address))
        message = "Новый вызов: ${incident.title}"
    }

    fun isBusy(unit: Vehicle) = alarms.any { it.unit == unit.callSign && it.status == CallStatus.ASSIGNED }

    fun assign(alarm: Alarm) {
        val unit = units.firstOrNull { !isBusy(it) && it.canHandle(alarm.incident) }
        if (unit == null) {
            message = "Нет свободной бригады для #${alarm.id}"
            return
        }
        update(alarm.copy(status = CallStatus.ASSIGNED, unit = unit.callSign))
        message = "${unit.callSign} выехала на #${alarm.id}"
    }

    fun finish(alarm: Alarm) {
        update(alarm.copy(status = CallStatus.DONE))
        message = "Вызов #${alarm.id} завершён"
    }

    private fun update(alarm: Alarm) {
        val index = alarms.indexOfFirst { it.id == alarm.id }
        alarms[index] = alarm
    }

    fun filteredAlarms(query: String, hideDone: Boolean) = alarms.filter {
        val matches = it.address.contains(query, ignoreCase = true) || it.incident.title.contains(query, ignoreCase = true)
        matches && !(hideDone && it.status == CallStatus.DONE)
    }

    fun increment() {
        counter++
    }
}
