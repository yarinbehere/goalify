package be.yarin.goalify.presentation.WeeklyProgress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.yarin.goalify.common.Resource
import be.yarin.goalify.domain.usecase.GetWeeklyProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WeeklyProgressViewModel @Inject constructor(
    private val getWeeklyProgressUseCase: GetWeeklyProgressUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeeklyProgressState())
    val state = _state

    init {
        getWeeklyProgress()
    }

    private fun getWeeklyProgress() {
        getWeeklyProgressUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = WeeklyProgressState(weeklyProgress = result.data ?: emptyList())
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
}