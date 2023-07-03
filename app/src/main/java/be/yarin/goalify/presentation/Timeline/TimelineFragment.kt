package be.yarin.goalify.presentation.Timeline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.yarin.goalify.R
import be.yarin.goalify.presentation.WeeklyProgress.WeeklyProgressFragment
import be.yarin.goalify.presentation.WeeklyProgress.WeeklyProgressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimelineFragment : Fragment() {

    private val viewModel by activityViewModels<WeeklyProgressViewModel>()
    private lateinit var dailyTimelineProgressAdapter: TimelineProgressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                if (!state.isLoading && state.weeklyProgress.isNotEmpty()) {
                    dailyTimelineProgressAdapter.differ.submitList(state.weeklyProgress.toList())
                }
            }
        }

        view.findViewById<View>(R.id.btn_timeline_back)?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragment_container, WeeklyProgressFragment.newInstance())
                addToBackStack(this@TimelineFragment::class.java.name)
                commit()
            }
        }
    }

    private fun setupRecyclerView() {
        dailyTimelineProgressAdapter = TimelineProgressAdapter()
        view?.findViewById<RecyclerView>(R.id.rvDailyTimeline)?.apply {
            adapter = dailyTimelineProgressAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    companion object {
        const val TAG = "TimelineFragment"
        fun newInstance() = TimelineFragment()
    }
}