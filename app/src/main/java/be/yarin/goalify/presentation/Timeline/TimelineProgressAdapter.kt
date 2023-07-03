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
import android.util.Log
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

    inner class TimelineDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<DailyProgress>() {
        override fun areItemsTheSame(oldItem: DailyProgress, newItem: DailyProgress): Boolean {
            return oldItem.day_activity == newItem.day_activity
        }

        override fun areContentsTheSame(oldItem: DailyProgress, newItem: DailyProgress): Boolean {
            Log.d(TAG, "areContentsTheSame:  ${oldItem.day_activity}")
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineDayViewHolder {
        return TimelineDayViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.timeline_day_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimelineDayViewHolder, position: Int) {
        val dailyProgress = differ.currentList[position]
        holder.itemView.apply {

            rootView?.findViewById<TextView>(R.id.tvDayInMonthItem)?.text =
                position.plus(8).toString()
            rootView?.findViewById<TextView>(R.id.tvDayNameItem)?.text =
                Calendar.getInstance().getListOfDays()[position]

            val coloring = if (dailyProgress.day_activity >= dailyProgress.day_goal) {
                Color.parseColor("#00B93F")
            } else {
                Color.parseColor("#0284FD")
            }

            val stepsText = SpannableStringBuilder().apply {
                val start = length
                append("${dailyProgress.day_activity}")
                val end = length
                setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(ForegroundColorSpan(coloring), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }.append("/${dailyProgress.day_goal}")

            rootView?.findViewById<TextView>(R.id.tvDayProgressItem)?.text = stepsText

            val kcalText = SpannableStringBuilder().bold { append("${dailyProgress.day_calories}") }.append(" KCAL")
            rootView?.findViewById<TextView>(R.id.tvKCalItem)?.apply {
                text = kcalText
                if (dailyProgress.day_activity >= dailyProgress.day_goal) {
                    val kcalDrawable = tintDrawable(context, R.drawable.progress_list_item_indicator, coloring)
                    setCompoundDrawablesWithIntrinsicBounds(kcalDrawable, null, null, null)
                }
            }

            val distanceMeters = dailyProgress.day_distance
            val distanceText = if (distanceMeters >= 1000) {
                val distanceKm = distanceMeters / 1000.0
                SpannableStringBuilder().bold { append("%.2f".format(distanceKm)) }.append(" KM")
            } else {
                SpannableStringBuilder().bold { append("$distanceMeters") }.append(" M")
            }

            rootView?.findViewById<TextView>(R.id.tvMetersItem)?.apply {
                text = distanceText
                if (dailyProgress.day_activity >= dailyProgress.day_goal) {
                    val kcalDrawable = tintDrawable(context, R.drawable.progress_list_item_indicator, coloring)
                    setCompoundDrawablesWithIntrinsicBounds(kcalDrawable, null, null, null)
                }
            }

            rootView?.findViewById<CircularProgressIndicator>(R.id.circularProgressIndicator)?.apply {
                progress = dailyProgress.day_activity * 100 / dailyProgress.day_goal
                setIndicatorColor(coloring)
            }
            if (Calendar.getInstance().getCurrentDayFormatted() == Calendar.getInstance().getListOfDays()[position]) {
                rootView?.findViewById<View>(R.id.current_day_indicator)?.visibility = View.VISIBLE
            } else {
                rootView?.findViewById<View>(R.id.current_day_indicator)?.visibility = View.INVISIBLE
            }
        }
    }

    fun tintDrawable(context: Context, drawableResId: Int, color: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(context, drawableResId)?.mutate()
        drawable?.let { DrawableCompat.setTint(it, color) }
        return drawable
    }


    companion object {
        private const val TAG = "TimelineProgressAdapter"
    }
}
