package kz.itspace.fire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kz.itspace.fire.model.Alarm
import kz.itspace.fire.model.Ambulance
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.EntityRandomizer
import kz.itspace.fire.model.FireTruck
import kz.itspace.fire.model.Incident
import kz.itspace.fire.model.Vehicle
import kz.itspace.fire.model.busyUnits

class DispatchViewModel : ViewModel() {

    val units: List<Vehicle> = listOf(
        FireTruck("Пожарная-1"),
        FireTruck("Пожарная-2"),
        Ambulance("Скорая-1"),
        Ambulance("Скорая-2"),
    )

    private val _alarms = MutableStateFlow(List(3) { EntityRandomizer.randomAlarm(id = it + 1, minutesAgo = 7 - it * 3) })
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    val now: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1_000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), System.currentTimeMillis())

    private var nextId = _alarms.value.size + 1

    fun addAlarm(address: String, incident: Incident, phone: String, note: String): Int {
        val id = nextId++
        _alarms.update { listOf(Alarm(id, incident, address.trim(), phone.trim(), note.trim())) + it }
        return id
    }

    fun assign(id: Int, unit: Vehicle) {
        if (unit.callSign in _alarms.value.busyUnits()) return
        change(id) { it.copy(status = CallStatus.EN_ROUTE, unit = unit.callSign) }
    }

    fun arrive(id: Int) = change(id) { it.copy(status = CallStatus.ON_SITE) }

    fun finish(id: Int) = change(id) { it.copy(status = CallStatus.DONE) }

    private fun change(id: Int, transform: (Alarm) -> Alarm) {
        _alarms.update { list -> list.map { if (it.id == id) transform(it) else it } }
    }
}
