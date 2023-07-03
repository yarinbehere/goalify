package be.yarin.goalify.presentation.WeeklyProgress

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import be.yarin.goalify.common.Constants
import be.yarin.goalify.common.Resource
import be.yarin.goalify.domain.model.DailyProgress
import be.yarin.goalify.domain.usecase.GetWeeklyProgressUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@HiltViewModel
class WeeklyProgressViewModel @Inject constructor(
    private val getWeeklyProgressUseCase: GetWeeklyProgressUseCase,
    context: Application
) : AndroidViewModel(context) {

    private val _state = MutableStateFlow(WeeklyProgressState())
    val state = _state.asStateFlow()

    // TODO: Inefficient, should be done with a Room database
    private var sharedPreferences: SharedPreferences =
        getApplication<Application>().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

    init {
        val currentTime = System.currentTimeMillis()
        val fetchTime = sharedPreferences.getLong(PREF_KEY_FETCH_TIME, 0)
        val weeklyData = sharedPreferences.getString(PREF_KEY_DATA, "")

        // Calculate the time difference in hours
        val timeDifferenceHours = (currentTime - fetchTime).milliseconds.toDouble(DurationUnit.HOURS)

        if (weeklyData.isNullOrEmpty() || timeDifferenceHours >= Constants.CACHE_DURATION) {
            Log.d(TAG, "Loading data from remote")
            getWeeklyProgress()
        } else {
            Log.d(TAG, "Loading data from shared preferences")
            val weeklyProgress = Gson().fromJson(weeklyData, Array<DailyProgress>::class.java).toList()
            _state.value = WeeklyProgressState(weeklyProgress = weeklyProgress)
        }
    }

    private fun getWeeklyProgress() {
        getWeeklyProgressUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    // Update the state
                    _state.value = WeeklyProgressState(weeklyProgress = result.data ?: emptyList())
                    // Save the data to shared preferences
                    with(sharedPreferences.edit()) {
                        putString(PREF_KEY_DATA, Gson().toJson(result.data))
                        putLong(PREF_KEY_FETCH_TIME, System.currentTimeMillis())
                        apply()
                    }
                }
                is Resource.Error -> {
                    _state.value = WeeklyProgressState(error = result.message ?: "An unexpected error occured")
                }
                is Resource.Loading -> {
                    _state.value = WeeklyProgressState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        private const val TAG = "WeeklyProgressViewModel"
        private const val PREF_KEY = "cached_data"
        private const val PREF_KEY_DATA = "week_data"
        private const val PREF_KEY_FETCH_TIME = "fetch_time"
    }
}

fun Calendar.getCurrentDayFormatted(): String {
    return when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sun"
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> ""
    }
}

fun Calendar.getListOfDays(): List<String> {
    val days = mutableListOf<String>()
    val format = SimpleDateFormat("EEE", Locale.getDefault())

    this.firstDayOfWeek = Calendar.SUNDAY
    this.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

    for (i in 0..6) {
        days.add(format.format(this.time).capitalize(Locale.getDefault()))
        this.add(Calendar.DAY_OF_WEEK, 1)
    }
    return days
}