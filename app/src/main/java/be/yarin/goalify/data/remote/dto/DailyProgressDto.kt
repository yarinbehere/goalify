package be.yarin.goalify.data.remote.dto

import be.yarin.goalify.domain.model.DailyProgress

data class DailyProgressDto(
    val daily_data: DailyData,
    val daily_item: DailyItem
)

fun DailyProgressDto.toDomainModel(): DailyProgress {
    return DailyProgress(
        day_goal = daily_item.daily_goal,
        day_activity = daily_item.daily_activity,
        day_distance_meters = daily_data.daily_distance_meters,
        day_calories = daily_data.daily_kcal
    )
}