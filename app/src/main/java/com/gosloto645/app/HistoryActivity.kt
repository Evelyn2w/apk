package com.gosloto645.app

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class HistoryActivity : BaseActivity() {

    private var filter = 0
    private val filters = listOf("All","Today","This Week","This Month")
    private lateinit var listContainer: LinearLayout
    private lateinit var filterViews: List<TextView>
    private val draws = DataService.FALLBACK_DRAWS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density

        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F0F7FF"))
        }
        outer.addView(buildBar(dp))

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((12*dp).toInt(),(12*dp).toInt(),(12*dp).toInt(),(12*dp).toInt())
        }

        // Stat chips
        val chips = LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL
            layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        listOf(
            Pair("${draws.first().drawNumber}","Total Draws"),
            Pair("${draws.count { isToday(it) }}","Today"),
            Pair("2026","Since")
        ).forEachIndexed { i, (v, l) ->
            if (i>0) chips.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams((8*dp).toInt(),1) })
            chips.addView(buildChip(v,l,dp))
        }
        content.addView(chips)
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(10*dp).toInt()) })

        // Filter tabs
        val tabRow = HorizontalScrollView(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(38*dp).toInt()) }
        val tabInner = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        val tvList = mutableListOf<TextView>()
        filters.forEachIndexed { i, f ->
            val tab = TextView(this).apply {
                text=f; textSize=11f; gravity=Gravity.CENTER
                setPadding((14*dp).toInt(),(7*dp).toInt(),(14*dp).toInt(),(7*dp).toInt())
                val lp=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,(34*dp).toInt()); lp.marginEnd=(6*dp).toInt(); layoutParams=lp
                setOnClickListener { filter=i; tvList.forEachIndexed { j,v -> styleTab(v,j==i,dp) }; renderList(dp) }
            }
            styleTab(tab, i==0, dp); tvList.add(tab); tabInner.addView(tab)
        }
        filterViews = tvList
        tabRow.addView(tabInner); content.addView(tabRow)
        content.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(10*dp).toInt()) })

        listContainer = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        content.addView(listContainer)

        scroll.addView(content); outer.addView(scroll)
        setContentView(outer)
        renderList(dp)
    }

    private fun renderList(dp: Float) {
        listContainer.removeAllViews()
        getFiltered().forEach { draw ->
            listContainer.addView(buildRow(draw, dp))
            listContainer.addView(Space(this).apply { layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(7*dp).toInt()) })
        }
    }

    private fun getFiltered() = when(filter) {
        1 -> draws.filter { isToday(it) }
        2 -> draws.filter { isThisWeek(it) }
        3 -> draws.filter { isThisMonth(it) }
        else -> draws
    }

    private fun isToday(d: DrawResult) = d.date.contains("May 18")
    private fun isThisWeek(d: DrawResult) = d.date.contains("May 1")
    private fun isThisMonth(d: DrawResult) = d.date.contains("2026")

    private fun buildRow(draw: DrawResult, dp: Float): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor("#E0F2F1")); cornerRadius=10*dp; setStroke(2,Color.parseColor("#B2DFDB")) }
            setPadding((10*dp).toInt(),(9*dp).toInt(),(10*dp).toInt(),(9*dp).toInt())
        }
        val topR=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        topR.addView(TextView(this).apply { text=draw.time; textSize=12f; setTextColor(Color.parseColor("#00695C")); setTypeface(typeface,Typeface.BOLD) })
        topR.addView(TextView(this).apply { text="  ${draw.date}"; textSize=10f; setTextColor(Color.parseColor("#90A4AE")); val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f); layoutParams=lp })
        topR.addView(TextView(this).apply { text="#${draw.drawNumber}"; textSize=9f; setTextColor(Color.parseColor("#4DB6AC")) })
        row.addView(topR)
        val ballR=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; val lp=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT); lp.topMargin=(8*dp).toInt(); layoutParams=lp }
        draw.numbers.forEach { n ->
            val ball=BallView(this).apply { number=n; ballType=BallType.NORMAL; val sz=(30*dp).toInt(); val lp=LinearLayout.LayoutParams(sz,sz); lp.marginEnd=(4*dp).toInt(); layoutParams=lp }
            ballR.addView(ball)
        }
        row.addView(ballR)
        return row
    }

    private fun buildChip(v: String, l: String, dp: Float): LinearLayout {
        val chip=LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER
            background=android.graphics.drawable.GradientDrawable().apply { setColor(Color.parseColor("#E0F2F1")); cornerRadius=10*dp; setStroke(2,Color.parseColor("#B2DFDB")) }
            val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
            layoutParams=lp; setPadding(0,(8*dp).toInt(),0,(8*dp).toInt())
        }
        chip.addView(TextView(this).apply { text=v; textSize=14f; setTextColor(Color.parseColor("#00695C")); setTypeface(typeface,Typeface.BOLD); gravity=Gravity.CENTER })
        chip.addView(TextView(this).apply { text=l; textSize=9f; setTextColor(Color.parseColor("#4DB6AC")); gravity=Gravity.CENTER })
        return chip
    }

    private fun styleTab(tab: TextView, active: Boolean, dp: Float) {
        tab.setTextColor(if(active) Color.WHITE else Color.parseColor("#00695C"))
        tab.background=android.graphics.drawable.GradientDrawable().apply {
            setColor(if(active) Color.parseColor("#00695C") else Color.parseColor("#E0F2F1"))
            cornerRadius=8*dp; setStroke(2,Color.parseColor(if(active) "#00695C" else "#B2DFDB"))
        }
    }

    private fun buildBar(dp: Float): LinearLayout {
        val ab=LinearLayout(this).apply {
            orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#00695C"))
            val statusH=(24*dp).toInt()
            setPadding((14*dp).toInt(),statusH+(8*dp).toInt(),(14*dp).toInt(),(10*dp).toInt())
        }
        val back=TextView(this).apply { text="←"; textSize=20f; setTextColor(Color.WHITE); setOnClickListener { finish() } }
        val t=TextView(this).apply { text="Draw History 📋"; textSize=17f; setTextColor(Color.WHITE); setTypeface(typeface,Typeface.BOLD); val lp=LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f); lp.marginStart=(10*dp).toInt(); layoutParams=lp }
        ab.addView(back); ab.addView(t)
        return ab
    }
}
