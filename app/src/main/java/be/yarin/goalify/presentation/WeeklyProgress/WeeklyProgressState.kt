package be.yarin.goalify.presentation.WeeklyProgress

import be.yarin.goalify.domain.model.DailyProgress

data class WeeklyProgressState(
    val isLoading: Boolean = false,
    val weeklyProgress: List<DailyProgress> = emptyList(),
    val error: String = ""
)