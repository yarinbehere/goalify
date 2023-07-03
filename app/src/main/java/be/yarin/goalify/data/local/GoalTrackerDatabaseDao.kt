package be.yarin.goalify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalTrackerDatabaseDao {

    @Query("SELECT * FROM week_progress_tbl")
    fun getAll(): Flow<WeekProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(weeklyProgress: WeekProgressEntity)

    @Query("DELETE FROM week_progress_tbl")
    fun deleteAll()

}