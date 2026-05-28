package com.gosloto645.app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*

abstract class PredictionActivity : BaseActivity() {
    abstract val screenTitle: String
    abstract val headerColor: String
    abstract val cardColor: String
    abstract val cardLight: String
    abstract val cardBorder: String
    abstract val ballType: BallType
    abstract val times: List<String>
    abstract fun getPrediction(preds: Pair<List<Int>, List<Int>>): List<Int>
    abstract fun getDraws(all: List<DrawResult>): List<DrawResult>

    private lateinit var ballsContainer: LinearLayout
    private var selectedTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        val draws = DataService.FALLBACK_DRAWS
        val myDraws = getDraws(draws)

        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F0F7FF"))
        }
        outer.addView(buildBar(screenTitle, headerColor, dp))

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((12*dp).toInt(),(12*dp).toInt(),(12*dp).toInt(),(12*dp).toInt())
        }

        // Source badge
        val badge = LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL
            background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor(cardLight)); cornerRadius=8*dp; setStroke(2,Color.parseColor(cardBorder)) }
            setPadding((10*dp).toInt(),(8*dp).toInt(),(10*dp).toInt(),(8*dp).toInt())
        }
        badge.addView(TextView(this).apply { text="🌐"; textSize=14f })
        val badgeText = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f); lp.marginStart=(8*dp).toInt(); layoutParams=lp
        }
        badgeText.addView(TextView(this).apply { text="Live data from gosloto6x45.com"; textSize=10f; setTextColor(Color.parseColor(headerColor)); setTypeface(typeface,Typeface.BOLD) })
        badgeText.addView(TextView(this).apply { text="Predictions fetched directly from website"; textSize=9f; setTextColor(Color.parseColor(cardColor)) })
        badge.addView(badgeText)
        badge.addView(TextView(this).apply { text="●"; textSize=10f; setTextColor(Color.parseColor("#66BB6A")) })
        content.addView(badge)
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(10*dp).toInt()) })

        // Time tabs
        val tabRow = HorizontalScrollView(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(40*dp).toInt()) }
        val tabInner = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        val tabViews = mutableListOf<TextView>()
        times.forEachIndexed { i, t ->
            val tab = TextView(this).apply {
                text=t; textSize=11f; gravity=Gravity.CENTER
                setPadding((14*dp).toInt(),(7*dp).toInt(),(14*dp).toInt(),(7*dp).toInt())
                val lp=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,(34*dp).toInt()); lp.marginEnd=(6*dp).toInt(); layoutParams=lp
                setOnClickListener { selectedTime=i; tabViews.forEachIndexed { j, v -> updateTab(v, j==i, dp) } }
            }
            updateTab(tab, i==0, dp)
            tabViews.add(tab); tabInner.addView(tab)
        }
        tabRow.addView(tabInner); content.addView(tabRow)
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(10*dp).toInt()) })

        // Prediction card
        val predCard = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor(headerColor)); cornerRadius=14*dp }
            setPadding((14*dp).toInt(),(14*dp).toInt(),(14*dp).toInt(),(14*dp).toInt())
        }
        val predTop = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        val predLeft = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            layoutParams=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
        }
        predLeft.addView(TextView(this).apply { text="PREDICTION"; textSize=10f; setTextColor(Color.parseColor("#FFCCBC")); letterSpacing=0.05f })
        predLeft.addView(TextView(this).apply { text="🌐 gosloto6x45.com"; textSize=9f; setTextColor(Color.parseColor("#FFAB91")) })
        val predRight = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.END }
        predRight.addView(TextView(this).apply { text=times[0]; textSize=14f; setTextColor(Color.WHITE); setTypeface(typeface,Typeface.BOLD) })
        predRight.addView(TextView(this).apply { text="MSK Draw"; textSize=9f; setTextColor(Color.parseColor("#FFCCBC")) })
        predTop.addView(predLeft); predTop.addView(predRight)
        predCard.addView(predTop)

        ballsContainer = LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER
            val lp=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); lp.topMargin=(14*dp).toInt(); layoutParams=lp
        }
        predCard.addView(ballsContainer)
        predCard.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(10*dp).toInt()) })
        predCard.addView(TextView(this).apply { text="For entertainment only. Lottery draws are random events."; textSize=9f; setTextColor(Color.parseColor("#FFCCBC")); gravity=Gravity.CENTER })
        content.addView(predCard)

        // Show fallback balls immediately
        renderBalls(getPrediction(DataService.getFallbackPredictions()))

        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(12*dp).toInt()) })

        // Draw results below
        val resTitle = TextView(this).apply { text="Today's Results"; textSize=12f; setTextColor(Color.parseColor(headerColor)); setTypeface(typeface,Typeface.BOLD) }
        content.addView(resTitle)
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(8*dp).toInt()) })

        myDraws.take(5).forEach { draw ->
            content.addView(buildDrawRow(draw, dp))
            content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(7*dp).toInt()) })
        }

        scroll.addView(content); outer.addView(scroll)
        setContentView(outer)

        // Fetch real predictions in background
        Thread {
            val preds = DataService.fetchPredictions()
            val myPred = getPrediction(preds)
            runOnUiThread { renderBalls(myPred) }
        }.start()
    }

    private fun renderBalls(numbers: List<Int>) {
        val dp = resources.displayMetrics.density
        ballsContainer.removeAllViews()
        numbers.take(6).forEach { n ->
            val ball = BallView(this).apply {
                number=n; ballType=this@PredictionActivity.ballType
                val sz=(42*dp).toInt(); val lp=LinearLayout.LayoutParams(sz,sz); lp.marginEnd=(6*dp).toInt(); layoutParams=lp
            }
            ballsContainer.addView(ball)
        }
    }

    private fun updateTab(tab: TextView, active: Boolean, dp: Float) {
        tab.setTextColor(if(active) Color.WHITE else Color.parseColor(headerColor))
        tab.setTypeface(tab.typeface, if(active) Typeface.BOLD else Typeface.NORMAL)
        tab.background = android.graphics.drawable.GradientDrawable().apply {
            setColor(if(active) Color.parseColor(headerColor) else Color.parseColor(cardLight))
            cornerRadius=8*dp; setStroke(2,Color.parseColor(if(active) headerColor else cardBorder))
        }
    }

    private fun buildDrawRow(draw: DrawResult, dp: Float): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor(cardLight)); cornerRadius=10*dp; setStroke(2,Color.parseColor(cardBorder)) }
            setPadding((10*dp).toInt(),(9*dp).toInt(),(10*dp).toInt(),(9*dp).toInt())
        }
        val topR = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        topR.addView(TextView(this).apply { text=draw.time; textSize=12f; setTextColor(Color.parseColor(headerColor)); setTypeface(typeface,Typeface.BOLD) })
        topR.addView(TextView(this).apply { text="  ${draw.date}"; textSize=10f; setTextColor(Color.parseColor("#90A4AE")); val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f); layoutParams=lp })
        topR.addView(TextView(this).apply { text="#${draw.drawNumber}"; textSize=9f; setTextColor(Color.parseColor(cardColor)) })
        row.addView(topR)
        val ballR = LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL
            val lp=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); lp.topMargin=(8*dp).toInt(); layoutParams=lp
        }
        draw.numbers.forEach { n ->
            val ball=BallView(this).apply { number=n; ballType=this@PredictionActivity.ballType; val sz=(30*dp).toInt(); val lp=LinearLayout.LayoutParams(sz,sz); lp.marginEnd=(4*dp).toInt(); layoutParams=lp }
            ballR.addView(ball)
        }
        row.addView(ballR)
        return row
    }

    fun buildBar(title: String, color: String, dp: Float): LinearLayout {
        val ab = LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor(color))
            val statusH=(24*dp).toInt()
            setPadding((14*dp).toInt(),statusH+(8*dp).toInt(),(14*dp).toInt(),(10*dp).toInt())
        }
        val back=TextView(this).apply { text="←"; textSize=20f; setTextColor(Color.WHITE); setOnClickListener { finish() } }
        val t=TextView(this).apply { text=title; textSize=17f; setTextColor(Color.WHITE); setTypeface(typeface,Typeface.BOLD); val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f); lp.marginStart=(10*dp).toInt(); layoutParams=lp }
        ab.addView(back); ab.addView(t)
        return ab
    }
}

// Expose fallback from DataService
fun DataService.getFallbackPredictions() = Pair(listOf(9,18,22,34,38,45), listOf(7,15,27,31,34,43))

class MorningPredActivity : PredictionActivity() {
    override val screenTitle = "Morning Predictions ☀️"
    override val headerColor = "#E65100"
    override val cardColor = "#FB8C00"
    override val cardLight = "#FFF3E0"
    override val cardBorder = "#FFCC80"
    override val ballType = BallType.MORNING
    override val times = listOf("10:00","11:30","13:25","13:40","13:55")
    override fun getPrediction(preds: Pair<List<Int>,List<Int>>) = preds.first
    override fun getDraws(all: List<DrawResult>) = all.filter { it.isMorning }
}

class EveningPredActivity : PredictionActivity() {
    override val screenTitle = "Evening Predictions 🌙"
    override val headerColor = "#1A237E"
    override val cardColor = "#3949AB"
    override val cardLight = "#E8EAF6"
    override val cardBorder = "#C5CAE9"
    override val ballType = BallType.EVENING
    override val times = listOf("16:30","18:25","18:40","18:55","22:00","22:59")
    override fun getPrediction(preds: Pair<List<Int>,List<Int>>) = preds.second
    override fun getDraws(all: List<DrawResult>) = all.filter { !it.isMorning }
}
