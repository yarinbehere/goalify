package be.yarin.goalify.data.repository

import be.yarin.goalify.data.remote.GoalTrackerApi
import be.yarin.goalify.domain.repository.GoalTrackerRepository
import javax.inject.Inject

class GoalTrackerRepositoryImpl @Inject constructor(
    private val goalTrackerApi: GoalTrackerApi
): GoalTrackerRepository {
    override suspend fun getWeeklyProgress() = goalTrackerApi.getDailyData()
}