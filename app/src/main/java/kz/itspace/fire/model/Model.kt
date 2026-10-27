package kz.itspace.fire.model

import kotlin.random.Random

enum class CallStatus(val label: String) {
    NEW("ожидает"),
    EN_ROUTE("в пути"),
    ON_SITE("на месте"),
    DONE("завершён"),
}

sealed class Incident(val title: String, val emoji: String) {
    data class Fire(val floor: Int) : Incident("Пожар", "🔥")
    data class Accident(val injured: Int) : Incident("ДТП", "🚗")
    data class Medical(val critical: Boolean) : Incident("Медицина", "🚑")

    fun details(): String = when (this) {
        is Fire -> "этаж $floor"
        is Accident -> "пострадавших: $injured"
        is Medical -> if (critical) "критическое состояние" else "стабильное состояние"
    }
}

data class Alarm(
    val id: Int,
    val incident: Incident,
    val address: String,
    val phone: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: CallStatus = CallStatus.NEW,
    val unit: String? = null,
) {
    val inProgress get() = status == CallStatus.EN_ROUTE || status == CallStatus.ON_SITE
}

abstract class Vehicle(val callSign: String) {
    abstract val emoji: String
    abstract fun canHandle(incident: Incident): Boolean
}

class FireTruck(callSign: String) : Vehicle(callSign) {
    override val emoji = "🚒"
    override fun canHandle(incident: Incident) = incident !is Incident.Medical
}

class Ambulance(callSign: String) : Vehicle(callSign) {
    override val emoji = "🚑"
    override fun canHandle(incident: Incident) = incident !is Incident.Fire
}

fun List<Alarm>.queue(query: String, showDone: Boolean): List<Alarm> = this
    .filter { showDone || it.status != CallStatus.DONE }
    .filter { it.address.contains(query, ignoreCase = true) || it.incident.title.contains(query, ignoreCase = true) }
    .sortedWith(compareBy({ it.status.ordinal }, { it.createdAt }))

fun List<Alarm>.busyUnits(): Set<String> = filter { it.inProgress }.mapNotNull { it.unit }.toSet()

fun formatElapsed(ms: Long): String {
    val s = ms.coerceAtLeast(0) / 1000
    return if (s < 3600) "%d:%02d".format(s / 60, s % 60) else "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60)
}

object EntityRandomizer {
    private val streets = listOf("пр. Абая", "ул. Толе би", "пр. Аль-Фараби", "ул. Жандосова", "ул. Сатпаева")

    fun randomAddress() = "${streets.random()}, ${Random.nextInt(1, 200)}"

    fun randomPhone() = "+7 70${Random.nextInt(1, 8)} ${Random.nextInt(100, 1000)} ${Random.nextInt(10, 100)} ${Random.nextInt(10, 100)}"

    fun randomIncident(): Incident = when (Random.nextInt(3)) {
        0 -> Incident.Fire(floor = Random.nextInt(1, 17))
        1 -> Incident.Accident(injured = Random.nextInt(1, 4))
        else -> Incident.Medical(critical = Random.nextBoolean())
    }

    fun randomAlarm(id: Int, minutesAgo: Int) = Alarm(
        id = id,
        incident = randomIncident(),
        address = randomAddress(),
        phone = randomPhone(),
        createdAt = System.currentTimeMillis() - minutesAgo * 60_000L,
    )
}
