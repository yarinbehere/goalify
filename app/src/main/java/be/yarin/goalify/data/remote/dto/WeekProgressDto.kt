package be.yarin.goalify.data.remote.dto

import be.yarin.goalify.data.local.WeekProgressEntity

data class WeekProgressDto(
    val weekly_data: List<DailyProgressDto>
) {
    fun toWeeklyProgressEntity(): WeekProgressEntity {
        return WeekProgressEntity(
            id = System.currentTimeMillis(),
            weekly_data = weekly_data.mapIndexed { index, dailyProgressDto ->
                dailyProgressDto.toDomainModel(index)
            }
        )
    }
}