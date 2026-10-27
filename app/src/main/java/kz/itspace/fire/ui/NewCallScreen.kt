package kz.itspace.fire.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kz.itspace.fire.model.Incident

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCallScreen(onSave: (address: String, incident: Incident, phone: String, note: String) -> Unit, onBack: () -> Unit) {
    var type by rememberSaveable { mutableIntStateOf(0) }
    var address by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("1") }
    var critical by rememberSaveable { mutableStateOf(false) }
    var note by rememberSaveable { mutableStateOf("") }
    val types = listOf("🔥 Пожар", "🚗 ДТП", "🚑 Медицина")

    Scaffold(topBar = { TopAppBar(title = { Text("Новый вызов") }, navigationIcon = { BackButton(onBack) }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.forEachIndexed { index, label ->
                    FilterChip(selected = type == index, onClick = { type = index }, label = { Text(label) })
                }
            }
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон заявителя") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            if (type == 2) {
                Row(
                    modifier = Modifier.toggleable(critical, role = Role.Switch, onValueChange = { critical = it }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Switch(checked = critical, onCheckedChange = null)
                    Text("Критическое состояние")
                }
            } else {
                OutlinedTextField(
                    value = count,
                    onValueChange = { count = it.filter(Char::isDigit).take(3) },
                    label = { Text(if (type == 0) "Этаж" else "Пострадавших") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Комментарий") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                enabled = address.isNotBlank(),
                onClick = {
                    val n = count.toIntOrNull() ?: 1
                    val incident = when (type) {
                        0 -> Incident.Fire(floor = n)
                        1 -> Incident.Accident(injured = n)
                        else -> Incident.Medical(critical)
                    }
                    onSave(address, incident, phone, note)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Создать вызов")
            }
        }
    }
}
