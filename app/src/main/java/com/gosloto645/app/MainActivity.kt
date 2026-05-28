package com.gosloto645.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.cardview.widget.CardView

class MainActivity : BaseActivity() {

    private lateinit var draws: List<DrawResult>
    private lateinit var stats: NumberStats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        draws = DataService.FALLBACK_DRAWS
        stats = DataService.computeStats(draws)

        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F0F7FF"))
        }
        val dp = resources.displayMetrics.density

        // HEADER
        val header = buildHeader(dp)
        root.addView(header)

        // MENU ITEMS
        val menuItems = listOf(
            Triple("Latest Results",     "Live draw results updated every draw",  Intent(this, ResultsActivity::class.java)),
            Triple("Hot Balls 🔥",       "Most frequent numbers — last 100 draws", Intent(this, HotBallsActivity::class.java)),
            Triple("Cold Balls ❄️",      "Least frequent numbers",                Intent(this, ColdBallsActivity::class.java)),
            Triple("Overdue Balls ⏰",   "Numbers not drawn recently",            Intent(this, OverdueActivity::class.java)),
            Triple("Morning Predictions ☀️", "10:00–13:55 MSK draw predictions", Intent(this, MorningPredActivity::class.java)),
            Triple("Evening Predictions 🌙", "16:30–22:59 MSK draw predictions", Intent(this, EveningPredActivity::class.java)),
            Triple("History 📋",         "All past draw results",                 Intent(this, HistoryActivity::class.java)),
        )

        val padPx = (12*dp).toInt()
        val innerPad = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padPx, padPx, padPx, padPx)
        }

        menuItems.forEach { (title, sub, intent) ->
            innerPad.addView(buildMenuItem(title, sub, intent, dp))
            val space = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (8*dp).toInt())
            }
            innerPad.addView(space)
        }

        root.addView(innerPad)
        scroll.addView(root)
        setContentView(scroll)

        // Load fresh data in background
        Thread {
            draws = DataService.fetchResults()
            stats = DataService.computeStats(draws)
        }.start()
    }

    private fun buildHeader(dp: Float): LinearLayout {
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1565C0"))
            val padPx = (14*dp).toInt()
            setPadding(padPx, (resources.configuration.screenHeightDp * dp / 30 + padPx).toInt(), padPx, padPx)
        }

        // Top row
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val logoBox = TextView(this).apply {
            text = "G6"; textSize = 14f
            setTextColor(Color.parseColor("#1565C0"))
            setTypeface(typeface, Typeface.BOLD)
            setBackgroundResource(R.drawable.bg_logo_box)
            gravity = Gravity.CENTER
            val s = (36*dp).toInt()
            layoutParams = LinearLayout.LayoutParams(s, s)
            setPadding(4,4,4,4)
        }
        val titleCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            lp.marginStart = (10*dp).toInt()
            layoutParams = lp
        }
        val t1 = TextView(this).apply { text="Gosloto 6/45"; textSize=16f; setTextColor(Color.WHITE); setTypeface(typeface,Typeface.BOLD) }
        val t2 = TextView(this).apply { text="Russia National Lottery"; textSize=10f; setTextColor(Color.parseColor("#90CAF9")) }
        titleCol.addView(t1); titleCol.addView(t2)

        topRow.addView(logoBox); topRow.addView(titleCol)
        header.addView(topRow)

        // Stat chips
        val chips = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = (12*dp).toInt()
            layoutParams = lp
        }
        chips.addView(buildStatChip("${draws.size}", "Cached Draws", dp))
        chips.addView(Space(this).apply { layoutParams = LinearLayout.LayoutParams((8*dp).toInt(), 1) })
        chips.addView(buildStatChip("#${DataService.FALLBACK_DRAWS.first().drawNumber}", "Latest Draw", dp))
        chips.addView(Space(this).apply { layoutParams = LinearLayout.LayoutParams((8*dp).toInt(), 1) })
        chips.addView(buildStatChip("LIVE", "Status", dp, green = true))
        header.addView(chips)
        return header
    }

    private fun buildStatChip(value: String, label: String, dp: Float, green: Boolean = false): LinearLayout {
        val chip = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#F5F9FF"))
            val r = (10*dp).toInt()
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#F5F9FF")); cornerRadius = r.toFloat()
            }
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            layoutParams = lp
            setPadding(0, (8*dp).toInt(), 0, (8*dp).toInt())
        }
        val valTv = TextView(this).apply {
            text = value; textSize = 15f
            setTextColor(if (green) Color.parseColor("#66BB6A") else Color.parseColor("#1565C0"))
            setTypeface(typeface, Typeface.BOLD); gravity = Gravity.CENTER
        }
        val lblTv = TextView(this).apply {
            text = label; textSize = 9f; setTextColor(Color.parseColor("#90A4AE")); gravity = Gravity.CENTER
        }
        chip.addView(valTv); chip.addView(lblTv)
        return chip
    }

    private fun buildMenuItem(title: String, subtitle: String, intent: Intent, dp: Float): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#F5F9FF"))
                cornerRadius = (12*dp)
                setStroke(2, Color.parseColor("#E3F0FF"))
            }
            setPadding((12*dp).toInt(), (11*dp).toInt(), (12*dp).toInt(), (11*dp).toInt())
            isClickable = true; isFocusable = true
            setOnClickListener { startActivity(intent) }
        }
        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            lp.marginStart = (4*dp).toInt()
            layoutParams = lp
        }
        val tTitle = TextView(this).apply {
            text = title; textSize = 13f; setTextColor(Color.parseColor("#1A237E"))
            setTypeface(typeface, Typeface.BOLD)
        }
        val tSub = TextView(this).apply {
            text = subtitle; textSize = 10f; setTextColor(Color.parseColor("#90A4AE"))
        }
        textCol.addView(tTitle); textCol.addView(tSub)
        val arrow = TextView(this).apply { text = "›"; textSize = 20f; setTextColor(Color.parseColor("#BBDEFB")) }
        row.addView(textCol); row.addView(arrow)
        return row
    }
}
