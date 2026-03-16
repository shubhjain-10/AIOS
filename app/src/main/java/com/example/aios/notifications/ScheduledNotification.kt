data class ScheduledNotification(
    val id: Int,
    val message: String,
    val hour: Int,
    val minute: Int,
    val days: List<Int>
)