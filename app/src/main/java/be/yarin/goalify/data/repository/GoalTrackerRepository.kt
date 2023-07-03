package be.yarin.goalify.data.repository

import android.util.Log
import be.yarin.goalify.common.Resource
import be.yarin.goalify.data.local.GoalTrackerDatabaseDao
import be.yarin.goalify.data.remote.GoalTrackerApi
import be.yarin.goalify.data.remote.dto.DailyData
import be.yarin.goalify.data.remote.dto.DailyItem
import be.yarin.goalify.data.remote.dto.DailyProgressDto
import be.yarin.goalify.data.remote.dto.WeekProgressDto
import be.yarin.goalify.data.remote.dto.toDomainModel
import be.yarin.goalify.domain.model.DailyProgress
import be.yarin.goalify.domain.repository.GoalTrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GoalTrackerRepositoryImpl @Inject constructor(
    private val goalTrackerApi: GoalTrackerApi,
    private val goalTrackerDatabaseDao: GoalTrackerDatabaseDao,
) : GoalTrackerRepository {
    override suspend fun getWeeklyProgress() = goalTrackerApi.getDailyData()

}