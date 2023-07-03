package be.yarin.goalify.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import be.yarin.goalify.R
import be.yarin.goalify.presentation.Timeline.TimelineFragment
import be.yarin.goalify.presentation.WeeklyProgress.WeeklyProgressFragment
import be.yarin.goalify.presentation.WeeklyProgress.WeeklyProgressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<WeeklyProgressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                Log.d(TAG, "Weekly progress: $state")
                if (!state.isLoading && state.weeklyProgress.isNotEmpty()) {
                    Log.d(TAG, "Weekly progress: loaded")
                } else {
                    Log.d(TAG, "Weekly progress: loading")
                }
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeeklyProgressFragment.newInstance())
                .commitNow()
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}