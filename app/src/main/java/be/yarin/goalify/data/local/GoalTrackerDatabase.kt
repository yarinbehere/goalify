package be.yarin.goalify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date
import java.util.UUID

@Database(entities = [WeekProgressEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GoalTrackerDatabase: RoomDatabase() {
    abstract val goalTrackerDao: GoalTrackerDatabaseDao
}
