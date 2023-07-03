package be.yarin.goalify.presentation.Timeline

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.yarin.goalify.R
import be.yarin.goalify.domain.model.DailyProgress
import be.yarin.goalify.presentation.WeeklyProgress.getCurrentDayFormatted
import be.yarin.goalify.presentation.WeeklyProgress.getListOfDays
import com.google.android.material.progressindicator.CircularProgressIndicator

class TimelineProgressAdapter :
    RecyclerView.Adapter<TimelineProgressAdapter.TimelineDayViewHolder>() {

    // Inner class to create ViewHolder for RecyclerView
    inner class TimelineDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Defining DiffUtil callbacks to detect changes in the data list
    private val differCallback = object : DiffUtil.ItemCallback<DailyProgress>() {
        // Checks if items are same
        override fun areItemsTheSame(oldItem: DailyProgress, newItem: DailyProgress): Boolean {
            return oldItem.day_activity == newItem.day_activity
        }

        // Checks if contents of the items are same
        override fun areContentsTheSame(oldItem: DailyProgress, newItem: DailyProgress): Boolean {
            return oldItem == newItem
        }
    }

    // AsyncListDiffer that can calculate the difference between old and new lists in a background thread
    val differ = AsyncListDiffer(this, differCallback)

    // Inflates the item layout for ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineDayViewHolder {
        return TimelineDayViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.timeline_day_item,
                parent,
                false
            )
        )
    }

    // Returns size of the data list
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    // Binds data to the views in item layout
    override fun onBindViewHolder(holder: TimelineDayViewHolder, position: Int) {
        val dailyProgress = differ.currentList[position]
        holder.itemView.apply {

            // Set day in month and day name
            rootView?.findViewById<TextView>(R.id.tvDayInMonthItem)?.text = position.plus(8).toString()
            rootView?.findViewById<TextView>(R.id.tvDayNameItem)?.text = Calendar.getInstance().getListOfDays()[position]

            // Define color for daily activity progress
            val coloring = if (dailyProgress.day_activity >= dailyProgress.day_goal) {
                Color.parseColor("#00B93F")
            } else {
                Color.parseColor("#0284FD")
            }

            // Create spannable string for step text and set it
            val stepsText = createSpannableString(dailyProgress.day_activity, coloring).append("/${dailyProgress.day_goal}")
            rootView?.findViewById<TextView>(R.id.tvDayProgressItem)?.text = stepsText

            // Create kcal text and set it
            rootView?.findViewById<TextView>(R.id.tvKCalItem)?.apply {
                text = createKcalText(this, dailyProgress.day_calories, dailyProgress.day_activity, dailyProgress.day_goal, context, coloring)
            }

            // Create and set distance text
            rootView?.findViewById<TextView>(R.id.tvMetersItem)?.apply {
                text = createDistanceText(this, dailyProgress.day_distance, dailyProgress.day_activity, dailyProgress.day_goal, context, coloring)
            }

            // Update progress indicator
            updateProgressIndicator(dailyProgress.day_activity, dailyProgress.day_goal, coloring)

            // Mark the current day
            markCurrentDay(position)
        }
    }
    // Create a spannable string for step text
    private fun createSpannableString(dayActivity: Int, coloring: Int): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            val start = length
            append("$dayActivity")
            val end = length
            setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                ForegroundColorSpan(coloring),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    // Create kcal text
    private fun createKcalText(textView: TextView, dayCalories: Int, dayActivity: Int, dayGoal: Int, context: Context, coloring: Int): Spannable {
        val kcalText = SpannableStringBuilder().bold { append("$dayCalories") }
            .append(" KCAL")

        // If day activity is higher or equal to day goal, tint the drawable and add it to the TextView
        if (dayActivity >= dayGoal) {
            val kcalDrawable = tintDrawable(context, R.drawable.progress_list_item_indicator, coloring)
            // setCompoundDrawablesWithIntrinsicBounds accepts drawables as parameters.
            // Here it's assumed that your drawable is set to the start. Adjust this if your case is different.
            textView.setCompoundDrawablesWithIntrinsicBounds(kcalDrawable, null, null, null)
        }

        return kcalText
    }

    // Create and return distance text
    private fun createDistanceText(textView: TextView, distanceMeters: Int, dayActivity: Int, dayGoal: Int, context: Context, coloring: Int): Spannable {
        val distanceText = if (distanceMeters >= 1000) {
            val distanceKm = distanceMeters / 1000.0
            SpannableStringBuilder().bold { append("%.2f".format(distanceKm)) }.append(" KM")
        } else {
            SpannableStringBuilder().bold { append("$distanceMeters") }.append(" M")
        }

        // If day activity is higher or equal to day goal, tint the drawable and add it to the TextView
        if (dayActivity >= dayGoal) {
            val distanceDrawable = tintDrawable(context, R.drawable.progress_list_item_indicator, coloring)
            // setCompoundDrawablesWithIntrinsicBounds accepts drawables as parameters.
            // Here it's assumed that your drawable is set to the start. Adjust this if your case is different.
            textView.setCompoundDrawablesWithIntrinsicBounds(distanceDrawable, null, null, null)
        }

        return distanceText
    }

    // Update progress indicator
    private fun View.updateProgressIndicator(dayActivity: Int, dayGoal: Int, coloring: Int) {
        findViewById<CircularProgressIndicator>(R.id.circularProgressIndicator)?.apply {
            progress = dayActivity * 100 / dayGoal
            setIndicatorColor(coloring)
        }
    }

    // Mark the current day
    private fun View.markCurrentDay(position: Int) {
        val currentDayIndicator = findViewById<View>(R.id.current_day_indicator)
        if (Calendar.getInstance().getCurrentDayFormatted() == Calendar.getInstance().getListOfDays()[position]) {
            currentDayIndicator?.visibility = View.VISIBLE
        } else {
            currentDayIndicator?.visibility = View.INVISIBLE
        }
    }

    // Helper function to tint a drawable
    private fun tintDrawable(context: Context, drawableResId: Int, color: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(context, drawableResId)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, color) }
        return drawable
    }

    companion object {
        private const val TAG = "TimelineProgressAdapter"
    }
}
