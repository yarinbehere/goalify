package be.yarin.goalify.data.remote

import be.yarin.goalify.data.remote.dto.DailyProgressDto
import be.yarin.goalify.data.remote.dto.WeekProgressDto
import retrofit2.http.GET

interface GoalTrackerApi {
    @GET("nextandroid/daily_data")
    suspend fun getDailyData(): WeekProgressDto
}