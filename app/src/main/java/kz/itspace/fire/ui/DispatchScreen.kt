package kz.itspace.fire.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kz.itspace.fire.DispatchViewModel
import kz.itspace.fire.R
import kz.itspace.fire.model.EntityRandomizer
import kz.itspace.fire.model.Incident

@Composable
fun DispatchScreen(vm: DispatchViewModel, modifier: Modifier = Modifier) {
    var query by rememberSaveable { mutableStateOf("") }
    var hideDone by rememberSaveable { mutableStateOf(false) }

    val visibleAlarms = vm.filteredAlarms(query, hideDone)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.ic_fire), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Диспетчерская 112", style = MaterialTheme.typography.headlineSmall)
                    Text(vm.message)
                }
            }
        }
        item {
            CounterDemoCard(vm.counter, onIncrement = { vm.increment() })
        }
        item {
            NewAlarmForm(onAdd = { address, incident -> vm.addAlarm(address, incident) })
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Бригады", fontWeight = FontWeight.Bold)
                    vm.units.forEach { unit -> UnitRow(unit, vm.isBusy(unit)) }
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск по адресу или типу") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = hideDone, onCheckedChange = { hideDone = it })
                Text("Скрыть завершённые")
            }
        }
        items(visibleAlarms, key = { it.id }) { alarm ->
            AlarmCard(
                alarm = alarm,
                onAssign = { vm.assign(alarm) },
                onFinish = { vm.finish(alarm) },
            )
        }
    }
}

@Composable
fun NewAlarmForm(onAdd: (String, Incident) -> Unit) {
    var address by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableIntStateOf(0) }
    val types = listOf("🔥 Пожар", "🚗 ДТП", "🚑 Медицина")

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Новый вызов", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.forEachIndexed { index, label ->
                    FilterChip(selected = type == index, onClick = { type = index }, label = { Text(label) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = address.isNotBlank(),
                    onClick = {
                        val incident = when (type) {
                            0 -> Incident.Fire(floor = 1)
                            1 -> Incident.Accident(injured = 1)
                            else -> Incident.Medical(critical = false)
                        }
                        onAdd(address, incident)
                        address = ""
                    },
                ) {
                    Text("Добавить")
                }
                OutlinedButton(onClick = { onAdd(EntityRandomizer.randomAddress(), EntityRandomizer.randomIncident()) }) {
                    Text("Случайный")
                }
            }
        }
    }
}
