package be.yarin.goalify.di

import android.content.Context
import androidx.room.Room
import be.yarin.goalify.common.Constants
import be.yarin.goalify.data.local.Converters
import be.yarin.goalify.data.local.GoalTrackerDatabaseDao
import be.yarin.goalify.data.local.GoalTrackerDatabase
import be.yarin.goalify.data.local.GsonParser
import be.yarin.goalify.data.local.JsonParser
import be.yarin.goalify.data.remote.GoalTrackerApi
import be.yarin.goalify.data.repository.GoalTrackerRepositoryImpl
import be.yarin.goalify.domain.repository.GoalTrackerRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideGoalTrackerDao(goalTrackerDatabase: GoalTrackerDatabase): GoalTrackerDatabaseDao {
        return goalTrackerDatabase.goalTrackerDao
    }
    @Provides
    @Singleton
    fun provideGoalTrackerRepository(api: GoalTrackerApi, db: GoalTrackerDatabaseDao): GoalTrackerRepository {
        return GoalTrackerRepositoryImpl(api, db)
    }
    @Provides
    @Singleton
    fun provideGoalTrackerDatabase(@ApplicationContext context: Context): GoalTrackerDatabase =
        Room.databaseBuilder(
            context,
            GoalTrackerDatabase::class.java,
            "goal_tracker_db",
        ).fallbackToDestructiveMigration()
            .addTypeConverter(Converters(GsonParser(Gson())))
            .build()
}