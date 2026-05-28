package com.gosloto645.app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ResultsActivity : BaseActivity() {

    private lateinit var container: LinearLayout
    private lateinit var swipe: SwipeRefreshLayout
    private var draws = DataService.FALLBACK_DRAWS
    private var stats = DataService.computeStats(DataService.FALLBACK_DRAWS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density

        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F0F7FF"))
        }

        // AppBar
        val ab = buildAppBar("Latest Results", Color.parseColor("#1565C0"), dp)
        outer.addView(ab)

        swipe = SwipeRefreshLayout(this).apply {
            setColorSchemeColors(Color.parseColor("#1565C0"))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val scroll = ScrollView(this)
        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((12*dp).toInt(), (12*dp).toInt(), (12*dp).toInt(), (12*dp).toInt())
        }
        scroll.addView(container)
        swipe.addView(scroll)
        outer.addView(swipe)
        setContentView(outer)

        swipe.setOnRefreshListener { loadData() }
        renderDraws()
        loadData()
    }

    private fun loadData() {
        swipe.isRefreshing = true
        Thread {
            draws = DataService.fetchResults()
            stats = DataService.computeStats(draws)
            runOnUiThread { swipe.isRefreshing = false; renderDraws() }
        }.start()
    }

    private fun renderDraws() {
        val dp = resources.displayMetrics.density
        container.removeAllViews()

        draws.forEachIndexed { idx, draw ->
            container.addView(buildDrawCard(draw, idx == 0, dp))
            container.addView(Space(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (8*dp).toInt()) })
        }
    }

    private fun buildDrawCard(draw: DrawResult, isLatest: Boolean, dp: Float): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(if (isLatest) Color.parseColor("#1565C0") else Color.parseColor("#F5F9FF"))
                cornerRadius = (if (isLatest) 14 else 10) * dp
                if (!isLatest) setStroke(2, Color.parseColor("#E3F0FF"))
            }
            setPadding((if(isLatest) 14 else 10)*dp.toInt(), (if(isLatest) 12 else 9)*dp.toInt(),
                (if(isLatest) 14 else 10)*dp.toInt(), (if(isLatest) 12 else 9)*dp.toInt())
        }

        // Top row: LATEST badge + time/date right
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        if (isLatest) {
            val badge = TextView(this).apply {
                text = "● LATEST"; textSize = 9f; setTextColor(Color.WHITE)
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.parseColor("#33FFFFFF")); cornerRadius = 20*dp
                }
                setPadding((10*dp).toInt(),(3*dp).toInt(),(10*dp).toInt(),(3*dp).toInt())
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            topRow.addView(badge)
        } else {
            val sp = Space(this).apply { layoutParams = LinearLayout.LayoutParams(0, 1, 1f) }
            topRow.addView(sp)
        }

        val timeCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.END }
        val timeColor = if (isLatest) Color.WHITE else Color.parseColor("#263238")
        val dateColor = if (isLatest) Color.parseColor("#90CAF9") else Color.parseColor("#90A4AE")
        timeCol.addView(TextView(this).apply { text=draw.time; textSize=13f; setTextColor(timeColor); setTypeface(typeface,Typeface.BOLD) })
        timeCol.addView(TextView(this).apply { text=draw.date; textSize=10f; setTextColor(dateColor) })
        timeCol.addView(TextView(this).apply { text="#${draw.drawNumber}"; textSize=9f; setTextColor(dateColor) })
        topRow.addView(timeCol)
        card.addView(topRow)

        // Balls row
        val ballsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = (10*dp).toInt()
            layoutParams = lp
        }
        val ballSize = if (isLatest) 44 else 32
        draw.numbers.forEach { n ->
            val type = if (isLatest) BallType.LATEST
            else if (stats.hot.take(5).contains(n)) BallType.HOT
            else if (stats.cold.take(5).contains(n)) BallType.COLD
            else BallType.NORMAL
            val ball = BallView(this).apply {
                number = n; ballType = type
                val sz = (ballSize*dp).toInt()
                val lp = LinearLayout.LayoutParams(sz, sz)
                lp.marginEnd = (5*dp).toInt()
                layoutParams = lp
            }
            ballsRow.addView(ball)
        }
        card.addView(ballsRow)
        return card
    }

    private fun buildAppBar(title: String, color: Int, dp: Float): LinearLayout {
        val ab = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(color)
            val statusH = (24*dp).toInt()
            setPadding((14*dp).toInt(), statusH+(8*dp).toInt(), (14*dp).toInt(), (10*dp).toInt())
        }
        val back = TextView(this).apply {
            text="←"; textSize=20f; setTextColor(Color.WHITE)
            setOnClickListener { finish() }
        }
        val t = TextView(this).apply {
            text=title; textSize=17f; setTextColor(Color.WHITE)
            setTypeface(typeface,Typeface.BOLD)
            val lp = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
            lp.marginStart=(10*dp).toInt(); layoutParams=lp
        }
        val ref = TextView(this).apply {
            text="↻"; textSize=18f; setTextColor(Color.WHITE)
            setOnClickListener { loadData() }
        }
        ab.addView(back); ab.addView(t); ab.addView(ref)
        return ab
    }
}
