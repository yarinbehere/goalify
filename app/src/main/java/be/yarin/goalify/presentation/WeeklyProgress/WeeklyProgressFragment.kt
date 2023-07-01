package be.yarin.goalify.presentation.WeeklyProgress

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import be.yarin.goalify.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeeklyProgressFragment : Fragment() {

    private lateinit var viewModel: WeeklyProgressViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weekly_progress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeeklyProgressViewModel::class.java)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                Log.d(TAG, "Weekly progress: $state")
            }
        }
    }

    companion object {
        private const val TAG = "WeeklyProgressFragment"
        fun newInstance() = WeeklyProgressFragment()
    }
}