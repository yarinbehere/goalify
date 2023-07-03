package be.yarin.goalify.data.remote.dto

import be.yarin.goalify.domain.model.DailyProgress

data class DailyProgressDto(
    val daily_data: DailyData,
    val daily_item: DailyItem
)

fun DailyProgressDto.toDomainModel(index: Int): DailyProgress {
    return DailyProgress(
        day_index = index,
        day_goal = daily_item.daily_goal,
        day_activity = daily_item.daily_activity,
        day_distance = daily_data.daily_distance_meters,
        day_calories = daily_data.daily_kcal
    )
}