package kz.itspace.fire.model

import kotlin.random.Random

enum class CallStatus(val label: String) {
    NEW("новый"),
    ASSIGNED("бригада в пути"),
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
    val status: CallStatus = CallStatus.NEW,
    val unit: String? = null,
)

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

object EntityRandomizer {
    private val streets = listOf("пр. Абая", "ул. Толе би", "пр. Аль-Фараби", "ул. Жандосова", "ул. Сатпаева")

    fun randomAddress() = "${streets.random()}, ${Random.nextInt(1, 200)}"

    fun randomIncident(): Incident = when (Random.nextInt(3)) {
        0 -> Incident.Fire(floor = Random.nextInt(1, 17))
        1 -> Incident.Accident(injured = Random.nextInt(1, 4))
        else -> Incident.Medical(critical = Random.nextBoolean())
    }
}
