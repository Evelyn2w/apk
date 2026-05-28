package com.gosloto645.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(0xFF1565C0.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val dp = resources.displayMetrics.density

        // Logo box
        val logoBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundResource(R.drawable.bg_logo_box)
            val s = (90 * dp).toInt()
            layoutParams = LinearLayout.LayoutParams(s, s)
            setPadding((12*dp).toInt(), (12*dp).toInt(), (12*dp).toInt(), (12*dp).toInt())
        }
        val logoG = TextView(this).apply {
            text = "G"; textSize = 32f; setTextColor(0xFF1565C0.toInt())
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }
        val logo6 = TextView(this).apply {
            text = "6/45"; textSize = 13f; setTextColor(0xFF1565C0.toInt())
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }
        logoBox.addView(logoG); logoBox.addView(logo6)

        val title = TextView(this).apply {
            text = "Gosloto 6/45"; textSize = 24f; setTextColor(0xFFFFFFFF.toInt())
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            gravity = android.view.Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = (18*dp).toInt()
            layoutParams = lp
        }
        val subtitle = TextView(this).apply {
            text = "Russia National Lottery"; textSize = 13f; setTextColor(0xFF90CAF9.toInt())
            gravity = android.view.Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = (4*dp).toInt()
            layoutParams = lp
        }
        val progress = ProgressBar(this).apply {
            isIndeterminate = true
            indeterminateTintList = android.content.res.ColorStateList.valueOf(0xFF90CAF9.toInt())
            val lp = LinearLayout.LayoutParams((24*dp).toInt(), (24*dp).toInt())
            lp.topMargin = (28*dp).toInt()
            layoutParams = lp
        }
        val loadText = TextView(this).apply {
            text = "Loading results..."; textSize = 12f; setTextColor(0xFF90CAF9.toInt())
            gravity = android.view.Gravity.CENTER
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = (8*dp).toInt()
            layoutParams = lp
        }

        root.addView(logoBox); root.addView(title); root.addView(subtitle)
        root.addView(progress); root.addView(loadText)
        setContentView(root)

        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 800 }
        root.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }
}
