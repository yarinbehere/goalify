package be.yarin.goalify.domain.repository

import be.yarin.goalify.common.Resource
import be.yarin.goalify.data.remote.dto.DailyProgressDto
import be.yarin.goalify.data.remote.dto.WeekProgressDto
import be.yarin.goalify.domain.model.DailyProgress
import kotlinx.coroutines.flow.Flow

interface GoalTrackerRepository {
    suspend fun getWeeklyProgress(): WeekProgressDto
}