package be.yarin.goalify.presentation.WeeklyProgress

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.util.Calendar
import android.util.Log
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

internal class CustomXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun drawLabel(
        c: Canvas,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF,
        angleDegrees: Float
    ) {
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)

        if (formattedLabel == Calendar.getInstance().getCurrentDayFormatted()) {
            val linePaint = Paint().apply {
                color = Color.parseColor("#0284FD")
                strokeWidth = 25f
            }
            val padding = 10f
            c.drawLine(
                x - mAxisLabelPaint.measureText(formattedLabel),
                y + mAxisLabelPaint.textSize + padding,
                x + mAxisLabelPaint.measureText(formattedLabel),
                y + mAxisLabelPaint.textSize + padding,
                linePaint
            )
        }
    }
}

