package be.yarin.goalify.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import be.yarin.goalify.R
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
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                if (!state.isLoading && state.weeklyProgress.isNotEmpty()) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeeklyProgressFragment.newInstance())
                .commitNow()
        }
    }
}