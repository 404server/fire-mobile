package kz.itspace.fire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.itspace.fire.model.Alarm
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.Incident
import kz.itspace.fire.model.Vehicle

@Composable
fun IncidentIcon(incident: Incident) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(incident.emoji, fontSize = 24.sp)
    }
}

@Composable
fun AlarmCard(alarm: Alarm, onAssign: () -> Unit, onFinish: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IncidentIcon(alarm.incident)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("#${alarm.id} ${alarm.incident.title}", fontWeight = FontWeight.Bold)
                    Text(alarm.address)
                }
                Text(alarm.status.label, color = MaterialTheme.colorScheme.primary)
            }
            if (expanded) {
                Text("Детали: ${alarm.incident.details()}")
                Text("Бригада: ${alarm.unit ?: "не назначена"}")
            }
            when (alarm.status) {
                CallStatus.NEW -> Button(onClick = onAssign, modifier = Modifier.fillMaxWidth()) {
                    Text("Назначить бригаду")
                }
                CallStatus.ASSIGNED -> Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                    Text("Завершить")
                }
                CallStatus.DONE -> {}
            }
        }
    }
}

@Composable
fun UnitRow(unit: Vehicle, busy: Boolean) {
    Row(Modifier.fillMaxWidth()) {
        Text("${unit.emoji} ${unit.callSign}", Modifier.weight(1f))
        Text(
            text = if (busy) "занята" else "свободна",
            color = if (busy) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun CounterDemoCard(viewModelCounter: Int, onIncrement: () -> Unit) {
    var rememberCounter by remember { mutableIntStateOf(0) }
    var saveableCounter by rememberSaveable { mutableIntStateOf(0) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Нажмите и поверните экран", fontWeight = FontWeight.Bold)
            Text("remember: $rememberCounter")
            Text("rememberSaveable: $saveableCounter")
            Text("ViewModel: $viewModelCounter")
            OutlinedButton(onClick = {
                rememberCounter++
                saveableCounter++
                onIncrement()
            }) {
                Text("+1 всем трём")
            }
        }
    }
}
