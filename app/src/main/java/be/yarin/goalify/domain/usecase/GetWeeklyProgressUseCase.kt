package be.yarin.goalify.domain.usecase

import android.content.Context
import android.util.Log
import be.yarin.goalify.R
import be.yarin.goalify.common.Resource
import be.yarin.goalify.data.local.GoalTrackerDatabaseDao
import be.yarin.goalify.data.remote.dto.toDomainModel
import be.yarin.goalify.domain.model.DailyProgress
import be.yarin.goalify.domain.repository.GoalTrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWeeklyProgressUseCase @Inject constructor(
    private val repository: GoalTrackerRepository,
    private val goalTrackerDatabaseDao: GoalTrackerDatabaseDao,
) {
    operator fun invoke(): Flow<Resource<List<DailyProgress>>> = flow {
        try {
            emit(Resource.Loading<List<DailyProgress>>())
            val weeklyProgress = repository.getWeeklyProgress().weekly_data.mapIndexed { index, dayData ->
                dayData.toDomainModel(index)
            }
            Log.d("GetWeeklyProgressUseCase", "weeklyProgress: $weeklyProgress")
            emit(Resource.Success<List<DailyProgress>>(weeklyProgress))
        } catch(e: Exception) {
            emit(Resource.Error<List<DailyProgress>>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }

    companion object {
        private const val TAG = "GetWeeklyProgressUseCase"
    }
}
