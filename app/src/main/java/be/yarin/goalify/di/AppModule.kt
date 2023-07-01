package be.yarin.goalify.di

import be.yarin.goalify.common.Constants
import be.yarin.goalify.data.remote.GoalTrackerApi
import be.yarin.goalify.data.repository.GoalTrackerRepositoryImpl
import be.yarin.goalify.domain.repository.GoalTrackerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGoalTrackerApi(): GoalTrackerApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoalTrackerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGoalTrackerRepository(api: GoalTrackerApi): GoalTrackerRepository {
        return GoalTrackerRepositoryImpl(api)
    }
}