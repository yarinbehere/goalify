package be.yarin.goalify.domain.model

data class DailyProgress(
    val day_index: Int,
    val day_goal: Int,
    val day_activity: Int,
    val day_distance: Int,
    val day_calories: Int,
)