package kz.itspace.fire.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.itspace.fire.DispatchViewModel
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.busyUnits
import kz.itspace.fire.model.formatElapsed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallScreen(vm: DispatchViewModel, id: Int, onBack: () -> Unit) {
    val alarms by vm.alarms.collectAsStateWithLifecycle()
    val now by vm.now.collectAsStateWithLifecycle()
    val alarm = alarms.firstOrNull { it.id == id }
    val busy = remember(alarms) { alarms.busyUnits() }
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Вызов #$id") }, navigationIcon = { BackButton(onBack) }) }) { padding ->
        if (alarm == null) {
            Text("Вызов не найден", Modifier.padding(padding).padding(16.dp))
            return@Scaffold
        }
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IncidentIcon(alarm.incident)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(alarm.incident.title, style = MaterialTheme.typography.titleLarge)
                            Text(alarm.incident.details())
                        }
                        Pill(alarm.status.label, alarm.status.color())
                    }
                    InfoLine("Адрес", alarm.address)
                    InfoLine("Телефон", alarm.phone.ifBlank { "—" })
                    if (alarm.note.isNotBlank()) InfoLine("Комментарий", alarm.note)
                    InfoLine("Принят", "${DateFormat.format("HH:mm", alarm.createdAt)} (${formatElapsed(now - alarm.createdAt)} назад)")
                    InfoLine("Бригада", alarm.unit ?: "не назначена")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { context.open(Intent.ACTION_DIAL, "tel:" + alarm.phone.filter { it.isDigit() || it == '+' }) },
                    enabled = alarm.phone.isNotBlank(),
                    modifier = Modifier.weight(1f),
                ) { Text("📞 Позвонить") }
                OutlinedButton(
                    onClick = { context.open(Intent.ACTION_VIEW, "geo:0,0?q=" + Uri.encode(alarm.address)) },
                    modifier = Modifier.weight(1f),
                ) { Text("🗺 Маршрут") }
            }
            when (alarm.status) {
                CallStatus.NEW -> {
                    Text("Назначить бригаду", fontWeight = FontWeight.Bold)
                    vm.units.filter { it.canHandle(alarm.incident) }.forEach { unit ->
                        val free = unit.callSign !in busy
                        Button(onClick = { vm.assign(alarm.id, unit) }, enabled = free, modifier = Modifier.fillMaxWidth()) {
                            Text("${unit.emoji} ${unit.callSign} — ${if (free) "свободна" else "занята"}")
                        }
                    }
                }
                CallStatus.EN_ROUTE -> Button(onClick = { vm.arrive(alarm.id) }, Modifier.fillMaxWidth()) {
                    Text("Прибыли на место")
                }
                CallStatus.ON_SITE -> Button(onClick = { vm.finish(alarm.id) }, Modifier.fillMaxWidth()) {
                    Text("Завершить вызов")
                }
                CallStatus.DONE -> {}
            }
        }
    }
}

private fun Context.open(action: String, uri: String) {
    runCatching { startActivity(Intent(action, uri.toUri())) }
        .onFailure { Toast.makeText(this, "Нет приложения для этого действия", Toast.LENGTH_SHORT).show() }
}
