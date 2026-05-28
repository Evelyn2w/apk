package com.gosloto645.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

enum class BallType { NORMAL, HOT, COLD, OVERDUE, LATEST, MORNING, EVENING }

class BallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var number: Int = 0
        set(value) { field = value; invalidate() }
    var ballType: BallType = BallType.NORMAL
        set(value) { field = value; invalidate() }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 4f }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val r = (minOf(width, height) / 2f) - 4f

        val (bg, border, text) = when (ballType) {
            BallType.HOT     -> Triple(Color.parseColor("#FFEBEE"), Color.parseColor("#FFCDD2"), Color.parseColor("#B71C1C"))
            BallType.COLD    -> Triple(Color.parseColor("#E8F5E9"), Color.parseColor("#A5D6A7"), Color.parseColor("#1B5E20"))
            BallType.OVERDUE -> Triple(Color.parseColor("#F3E5F5"), Color.parseColor("#CE93D8"), Color.parseColor("#4A148C"))
            BallType.LATEST  -> Triple(Color.WHITE, Color.parseColor("#90CAF9"), Color.parseColor("#1565C0"))
            BallType.MORNING -> Triple(Color.parseColor("#FFF8E1"), Color.parseColor("#FFD54F"), Color.parseColor("#E65100"))
            BallType.EVENING -> Triple(Color.parseColor("#E8EAF6"), Color.parseColor("#9FA8DA"), Color.parseColor("#1A237E"))
            BallType.NORMAL  -> Triple(Color.parseColor("#E3F0FF"), Color.parseColor("#90CAF9"), Color.parseColor("#0D47A1"))
        }

        bgPaint.color = bg
        borderPaint.color = border
        textPaint.color = text
        textPaint.textSize = r * 0.7f

        canvas.drawCircle(cx, cy, r, bgPaint)
        canvas.drawCircle(cx, cy, r, borderPaint)
        canvas.drawText("$number", cx, cy - (textPaint.ascent() + textPaint.descent()) / 2, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredWidth
        setMeasuredDimension(size, size)
    }
}
