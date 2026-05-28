package com.gosloto645.app

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity : AppCompatActivity() {

    fun setupToolbar(toolbar: Toolbar, title: String, showBack: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { onBackPressedDispatcher.onBackPressed(); return true }
        return super.onOptionsItemSelected(item)
    }

    fun makeBallRow(
        context: android.content.Context,
        numbers: List<Int>,
        hot: List<Int> = emptyList(),
        cold: List<Int> = emptyList(),
        forcedType: BallType? = null,
        sizeDp: Int = 38
    ): android.widget.LinearLayout {
        val row = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
        }
        val sizePx = (sizeDp * resources.displayMetrics.density).toInt()
        val marginPx = (4 * resources.displayMetrics.density).toInt()
        numbers.forEach { n ->
            val type = forcedType ?: when {
                hot.take(5).contains(n) -> BallType.HOT
                cold.take(5).contains(n) -> BallType.COLD
                else -> BallType.NORMAL
            }
            val ball = BallView(context).apply {
                number = n; ballType = type
                val lp = android.widget.LinearLayout.LayoutParams(sizePx, sizePx)
                lp.marginEnd = marginPx
                layoutParams = lp
            }
            row.addView(ball)
        }
        return row
    }
}
