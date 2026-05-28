package com.gosloto645.app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*

// ── HOT BALLS
class HotBallsActivity : BallStatsActivity() {
    override val screenTitle = "Hot Balls 🔥"
    override val headerColor = "#B71C1C"
    override val cardBg = "#FFEBEE"
    override val cardBorder = "#FFCDD2"
    override val ballType = BallType.HOT
    override val descTitle = "What are Hot Balls?"
    override val descText = "Numbers drawn most frequently in recent draws. Updated after every draw fetched from gosloto6x45.com"
    override fun getBalls(stats: NumberStats) = stats.hot
    override fun getFreqColor() = "#E57373"
    override fun getFreqBg() = "#FFEBEE"
}

// ── COLD BALLS
class ColdBallsActivity : BallStatsActivity() {
    override val screenTitle = "Cold Balls ❄️"
    override val headerColor = "#1B5E20"
    override val cardBg = "#E8F5E9"
    override val cardBorder = "#C8E6C9"
    override val ballType = BallType.COLD
    override val descTitle = "What are Cold Balls?"
    override val descText = "Numbers drawn least frequently in recent draws. Some players prefer these as they may be statistically due."
    override fun getBalls(stats: NumberStats) = stats.cold
    override fun getFreqColor() = "#66BB6A"
    override fun getFreqBg() = "#E8F5E9"
}

// ── OVERDUE BALLS
class OverdueActivity : BallStatsActivity() {
    override val screenTitle = "Overdue Balls ⏰"
    override val headerColor = "#4A148C"
    override val cardBg = "#F3E5F5"
    override val cardBorder = "#E1BEE7"
    override val ballType = BallType.OVERDUE
    override val descTitle = "What are Overdue Balls?"
    override val descText = "Numbers that have not appeared for the longest time. Gap analysis based on complete draw history."
    override fun getBalls(stats: NumberStats) = stats.overdue
    override fun getFreqColor() = "#AB47BC"
    override fun getFreqBg() = "#F3E5F5"
}

// ── BASE for ball stats screens
abstract class BallStatsActivity : BaseActivity() {
    abstract val screenTitle: String
    abstract val headerColor: String
    abstract val cardBg: String
    abstract val cardBorder: String
    abstract val ballType: BallType
    abstract val descTitle: String
    abstract val descText: String
    abstract fun getBalls(stats: NumberStats): List<Int>
    abstract fun getFreqColor(): String
    abstract fun getFreqBg(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        val draws = DataService.FALLBACK_DRAWS
        val stats = DataService.computeStats(draws)
        val balls = getBalls(stats)

        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F0F7FF"))
        }
        outer.addView(buildAppBar(screenTitle, headerColor, dp))

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((12*dp).toInt(),(12*dp).toInt(),(12*dp).toInt(),(12*dp).toInt())
        }

        // Ball grid card
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor(cardBg)); cornerRadius=14*dp
                setStroke(2,Color.parseColor(cardBorder))
            }
            setPadding((14*dp).toInt(),(14*dp).toInt(),(14*dp).toInt(),(14*dp).toInt())
        }
        val grid = GridLayout(this).apply {
            columnCount = 5
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin=(10*dp).toInt(); layoutParams=lp
        }
        val sz=(44*dp).toInt(); val m=(6*dp).toInt()
        balls.take(10).forEach { n ->
            val ball = BallView(this).apply {
                number=n; this.ballType=this@BallStatsActivity.ballType
                val lp=GridLayout.LayoutParams(); lp.width=sz; lp.height=sz
                lp.setMargins(m,m,m,m); layoutParams=lp
            }
            grid.addView(ball)
        }
        card.addView(grid); content.addView(card)

        // Frequency bars
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(12*dp).toInt()) })
        val freqTitle = TextView(this).apply {
            text="Frequency Chart"; textSize=12f
            setTextColor(Color.parseColor(headerColor)); setTypeface(typeface,Typeface.BOLD)
        }
        content.addView(freqTitle)
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(8*dp).toInt()) })

        val maxFreq = balls.mapNotNull { stats.frequency[it] }.maxOrNull() ?: 1
        balls.take(10).forEach { n ->
            val cnt = stats.frequency[n] ?: 0
            val pct = cnt.toFloat() / maxFreq
            val row = LinearLayout(this).apply {
                orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL
                val lp=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.bottomMargin=(6*dp).toInt(); layoutParams=lp
            }
            val numTv = TextView(this).apply {
                text="$n"; textSize=13f; setTextColor(Color.parseColor(headerColor))
                setTypeface(typeface,Typeface.BOLD); gravity=Gravity.END
                layoutParams=LinearLayout.LayoutParams((26*dp).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            val barBg = android.widget.FrameLayout(this).apply {
                background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor(getFreqBg())); cornerRadius=5*dp }
                val lp=LinearLayout.LayoutParams(0,(10*dp).toInt(),1f); lp.marginStart=(8*dp).toInt(); layoutParams=lp
            }
            val barFill = android.widget.FrameLayout(this).apply {
                background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor(getFreqColor())); cornerRadius=5*dp }
                layoutParams=android.widget.FrameLayout.LayoutParams((pct*1000).toInt(),(10*dp).toInt())
            }
            barBg.addView(barFill)
            val cntTv = TextView(this).apply {
                text="$cnt"; textSize=10f; setTextColor(Color.parseColor("#90A4AE"))
                val lp=LinearLayout.LayoutParams((22*dp).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT); lp.marginStart=(6*dp).toInt(); layoutParams=lp
            }
            row.addView(numTv); row.addView(barBg); row.addView(cntTv)
            content.addView(row)
        }

        // Description box
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(12*dp).toInt()) })
        val descBox = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor(cardBg)); cornerRadius=9*dp; setStroke(2,Color.parseColor(cardBorder)) }
            setPadding((10*dp).toInt(),(10*dp).toInt(),(10*dp).toInt(),(10*dp).toInt())
        }
        descBox.addView(TextView(this).apply { text=descTitle; textSize=11f; setTextColor(Color.parseColor(headerColor)); setTypeface(typeface,Typeface.BOLD) })
        descBox.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(4*dp).toInt()) })
        descBox.addView(TextView(this).apply { text=descText; textSize=10f; setTextColor(Color.parseColor(headerColor)); lineSpacingMultiplier=1.5f })
        content.addView(descBox)

        scroll.addView(content); outer.addView(scroll)
        setContentView(outer)
    }

    fun buildAppBar(title: String, color: String, dp: Float): LinearLayout {
        val ab = LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor(color))
            val statusH=(24*dp).toInt()
            setPadding((14*dp).toInt(),statusH+(8*dp).toInt(),(14*dp).toInt(),(10*dp).toInt())
        }
        val back = TextView(this).apply { text="←"; textSize=20f; setTextColor(Color.WHITE); setOnClickListener { finish() } }
        val t = TextView(this).apply {
            text=title; textSize=17f; setTextColor(Color.WHITE); setTypeface(typeface,Typeface.BOLD)
            val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f); lp.marginStart=(10*dp).toInt(); layoutParams=lp
        }
        ab.addView(back); ab.addView(t)
        return ab
    }
}
