package kz.itspace.fire.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.itspace.fire.DispatchViewModel
import kz.itspace.fire.R
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.busyUnits
import kz.itspace.fire.model.queue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchScreen(vm: DispatchViewModel, onOpenCall: (Int) -> Unit, onNewCall: () -> Unit) {
    val alarms by vm.alarms.collectAsStateWithLifecycle()
    val now by vm.now.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    var showDone by rememberSaveable { mutableStateOf(false) }

    val visible = remember(alarms, query, showDone) { alarms.queue(query, showDone) }
    val busy = remember(alarms) { alarms.busyUnits() }
    val waiting = alarms.count { it.status == CallStatus.NEW }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Диспетчерская 112") },
                navigationIcon = { Image(painterResource(R.drawable.ic_fire), null, Modifier.padding(horizontal = 8.dp)) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onNewCall) { Text("+ Новый вызов") }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Ожидают бригаду: $waiting · Свободно бригад: ${vm.units.size - busy.size} из ${vm.units.size}",
                        fontWeight = FontWeight.Bold,
                        color = if (waiting > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    )
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        vm.units.forEach { unit ->
                            val free = unit.callSign !in busy
                            Pill(
                                "${unit.emoji} ${unit.callSign}",
                                if (free) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                            )
                        }
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
                FilterChip(selected = showDone, onClick = { showDone = !showDone }, label = { Text("Показать завершённые") })
            }
            if (visible.isEmpty()) {
                item { Text("Вызовов нет", Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(visible, key = { it.id }) { alarm ->
                AlarmCard(alarm, now, onClick = { onOpenCall(alarm.id) })
            }
        }
    }
}
