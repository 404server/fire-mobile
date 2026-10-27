package kz.itspace.fire.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.itspace.fire.R
import kz.itspace.fire.model.Alarm
import kz.itspace.fire.model.CallStatus
import kz.itspace.fire.model.Incident
import kz.itspace.fire.model.formatElapsed

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
fun CallStatus.color(): Color = when (this) {
    CallStatus.NEW -> MaterialTheme.colorScheme.error
    CallStatus.EN_ROUTE -> MaterialTheme.colorScheme.secondary
    CallStatus.ON_SITE -> MaterialTheme.colorScheme.tertiary
    CallStatus.DONE -> MaterialTheme.colorScheme.outline
}

@Composable
fun Pill(text: String, color: Color) {
    Surface(color = color.copy(alpha = 0.15f), contentColor = color, shape = CircleShape) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AlarmCard(alarm: Alarm, now: Long, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IncidentIcon(alarm.incident)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("#${alarm.id} ${alarm.incident.title} · ${alarm.incident.details()}", fontWeight = FontWeight.Bold)
                Text(alarm.address)
                alarm.unit?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Pill(alarm.status.label, alarm.status.color())
                if (alarm.status != CallStatus.DONE) {
                    Text(formatElapsed(now - alarm.createdAt), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row {
        Text(label, Modifier.width(110.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Назад")
    }
}
