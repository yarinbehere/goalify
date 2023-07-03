package be.yarin.goalify.presentation.WeeklyProgress

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.icu.util.Calendar
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import be.yarin.goalify.domain.model.DailyProgress
import be.yarin.goalify.presentation.Timeline.TimelineFragment
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.Range
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log


@AndroidEntryPoint
class WeeklyProgressFragment : Fragment() {

    private val viewModel by activityViewModels<WeeklyProgressViewModel>()

    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(be.yarin.goalify.R.layout.fragment_weekly_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                Log.d(TAG, "Weekly progress: $state")
                if (!state.isLoading && state.weeklyProgress.isNotEmpty()) {
                    Log.d(TAG, "Weekly progress: loaded")
                    activity?.runOnUiThread {
                        drawChart(state.weeklyProgress)
                    }
                } else {
                    Log.d(TAG, "Weekly progress: loading")
                }
            }
        }

        // go to timeline fragment
        view.findViewById<View>(be.yarin.goalify.R.id.btn_weeklyprogress_timeline)?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                // add on top of the current fragment
                ?.replace(be.yarin.goalify.R.id.fragment_container, TimelineFragment.newInstance())
                ?.addToBackStack(this@WeeklyProgressFragment::class.java.name)
                ?.commit()
        }
    }

    fun drawChart(weeklyProgress: List<DailyProgress>) {
        // Define the days of the week
        val daysOfWeek = Calendar.getInstance().getListOfDays()

        Log.d(TAG, "daysOfWeek: $daysOfWeek")
        // Create bar data sets
        val barDataSetBlue =
            createBarDataSet(weeklyProgress, "Activity", Color.parseColor("#0284FD"), daysOfWeek)
        val barDataSetGreen =
            createBarDataSet(weeklyProgress, "Goal", Color.parseColor("#00B93F"), daysOfWeek)

        // Find the bar chart view and apply properties
        view?.findViewById<BarChart>(be.yarin.goalify.R.id.barChart)?.apply {
            // Set data and layout
            data = BarData(barDataSetBlue, barDataSetGreen)
            data.setDrawValues(false)
            applyChartLayout()

            // Group bars
            val groupSpace = 0.25f
            val barWidth = (1 - groupSpace) / 2
            barData.barWidth = barWidth
            groupBars(0f, groupSpace, 0f)

            applyLegendSettings()
            applyXAxisSettings(daysOfWeek, groupSpace, 0f)

            // Refresh the chart
            invalidate()
        }
    }

    // Helper function to create a bar data set
    private fun createBarDataSet(
        weeklyProgress: List<DailyProgress>,
        label: String,
        color: Int,
        daysOfWeek: List<String>
    ): BarDataSet {
        val barEntries = daysOfWeek.indices.map {
            BarEntry(
                it.toFloat(),
                if (label == "Activity") weeklyProgress[it].day_activity.toFloat() else weeklyProgress[it].day_goal.toFloat()
            )
        }
        return BarDataSet(barEntries, label).apply { this.color = color }
    }



    companion object {
        private const val TAG = "WeeklyProgressFragment"
        fun newInstance() = WeeklyProgressFragment()
    }
}


// Helper function to apply chart layout settings
private fun BarChart.applyChartLayout() {
    val topOffset = viewPortHandler.offsetTop()
    val rightOffset = viewPortHandler.offsetRight()
    val bottomOffset = viewPortHandler.offsetBottom()
    setViewPortOffsets(0f, topOffset, rightOffset, bottomOffset)
    extraLeftOffset = 0f
    setFitBars(true)
    axisLeft.isEnabled = false
    axisRight.isEnabled = false
    description.isEnabled = false
    axisLeft.axisMinimum = 0f
    axisRight.axisMinimum = 0f
    setScaleEnabled(false)
    setPinchZoom(false)
    setDrawBarShadow(false)
    setDrawGridBackground(false)
    val xValue = 0f
    val yValue = 0f
    centerViewTo(xValue, yValue, YAxis.AxisDependency.LEFT)
}

// Helper function to apply legend settings
private fun BarChart.applyLegendSettings() {
    legend.apply {
        verticalAlignment = Legend.LegendVerticalAlignment.TOP
        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        orientation = Legend.LegendOrientation.HORIZONTAL
        setDrawInside(false)
        form = Legend.LegendForm.CIRCLE
        formSize = 9f
        textSize = 11f
        xEntrySpace = 4f
    }
}

// Helper function to apply x-axis settings
private fun BarChart.applyXAxisSettings(
    daysOfWeek: List<String>,
    groupSpace: Float,
    barSpace: Float
) {
    xAxis.apply {
        setXAxisRenderer(
            CustomXAxisRenderer(
                viewPortHandler,
                xAxis,
                getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        valueFormatter = IndexAxisValueFormatter(daysOfWeek)
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
        labelCount = daysOfWeek.size
        granularity = 1f
        setCenterAxisLabels(true)
        val groupWidth = barData.getGroupWidth(groupSpace, barSpace)
        axisMaximum = daysOfWeek.size.toFloat() + groupWidth / 2f
    }
}

