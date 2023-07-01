package be.yarin.goalify.domain.repository

import be.yarin.goalify.data.remote.dto.DailyProgressDto
import be.yarin.goalify.data.remote.dto.WeekProgressDto

interface GoalTrackerRepository {
    suspend fun getWeeklyProgress(): WeekProgressDto
}