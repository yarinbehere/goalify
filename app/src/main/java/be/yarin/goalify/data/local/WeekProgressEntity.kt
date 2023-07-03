package be.yarin.goalify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import be.yarin.goalify.domain.model.DailyProgress
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Entity(tableName = "week_progress_tbl")
data class WeekProgressEntity (
    @PrimaryKey
    var id: Long = System.currentTimeMillis(),
    var weekly_data: List<DailyProgress> = listOf()
) {
    fun toDomainModel(): List<DailyProgress> {
        return weekly_data
    }
}

interface JsonParser {
    fun <T> fromJson(json: String, type: Type): T?
    fun <T> toJson(obj: T, type: Type): String?
}

class GsonParser(
    private val gson: com.google.gson.Gson
) : JsonParser {
    override fun <T> fromJson(json: String, type: Type): T? {
        return gson.fromJson(json, type)
    }

    override fun <T> toJson(obj: T, type: Type): String? {
        return gson.toJson(obj, type)
    }
}

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun fromWeeklyDataJson(json: String): List<DailyProgress> {
        return jsonParser.fromJson<ArrayList<DailyProgress>>(
            json,
            object : TypeToken<ArrayList<DailyProgress>>() {}.type
        ) ?: listOf()
    }

    @TypeConverter
    fun toWeeklyDataJson(weeklyData: List<DailyProgress>): String {
        return jsonParser.toJson(weeklyData, object : TypeToken<ArrayList<DailyProgress>>() {}.type)
            ?: "[]"
    }
}