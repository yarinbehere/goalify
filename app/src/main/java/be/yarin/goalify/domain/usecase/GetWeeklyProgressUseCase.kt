package be.yarin.goalify.domain.usecase

import be.yarin.goalify.common.Resource
import be.yarin.goalify.data.remote.dto.WeekProgressDto
import be.yarin.goalify.data.remote.dto.toDomainModel
import be.yarin.goalify.domain.model.DailyProgress
import be.yarin.goalify.domain.repository.GoalTrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWeeklyProgressUseCase @Inject constructor(
    private val repository: GoalTrackerRepository
) {
    operator fun invoke(): Flow<Resource<List<DailyProgress>>> = flow {
        try {
            emit(Resource.Loading<List<DailyProgress>>())
            val weeklyProgress = repository.getWeeklyProgress().weekly_data.map { it.toDomainModel() }
            emit(Resource.Success<List<DailyProgress>>(weeklyProgress))
        } catch(e: Exception) {
            emit(Resource.Error<List<DailyProgress>>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}