package com.gosloto645.app

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

data class DrawResult(
    val drawNumber: Int,
    val time: String,
    val date: String,
    val numbers: List<Int>,
    val isMorning: Boolean
)

data class NumberStats(
    val hot: List<Int>,
    val cold: List<Int>,
    val overdue: List<Int>,
    val frequency: Map<Int, Int>
)

object DataService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Fallback data
    val FALLBACK_DRAWS = listOf(
        DrawResult(17442,"18:40 MSK","May 18, 2026", listOf(11,21,27,29,40,45), false),
        DrawResult(17441,"18:25 MSK","May 18, 2026", listOf(3,4,15,17,22,36), false),
        DrawResult(17440,"16:30 MSK","May 18, 2026", listOf(19,20,27,29,37,45), false),
        DrawResult(17439,"13:55 MSK","May 18, 2026", listOf(9,10,26,30,34,42), true),
        DrawResult(17438,"13:40 MSK","May 18, 2026", listOf(2,5,9,12,22,28), true),
        DrawResult(17437,"13:25 MSK","May 18, 2026", listOf(9,15,26,34,40,42), true),
        DrawResult(17436,"11:30 MSK","May 18, 2026", listOf(1,2,31,34,36,39), true),
        DrawResult(17435,"10:00 MSK","May 18, 2026", listOf(6,11,21,27,28,43), true),
        DrawResult(17434,"22:59 MSK","May 17, 2026", listOf(3,9,23,30,31,35), false),
        DrawResult(17433,"22:00 MSK","May 17, 2026", listOf(7,19,21,32,34,36), false),
        DrawResult(17432,"18:55 MSK","May 17, 2026", listOf(2,8,12,14,18,43), false),
        DrawResult(17431,"18:40 MSK","May 17, 2026", listOf(10,13,15,20,23,44), false),
        DrawResult(17430,"18:25 MSK","May 17, 2026", listOf(10,14,17,31,34,36), false),
        DrawResult(17429,"16:30 MSK","May 17, 2026", listOf(3,7,9,28,33,44), false),
        DrawResult(17428,"13:55 MSK","May 17, 2026", listOf(14,15,16,20,33,37), true),
        DrawResult(17427,"13:40 MSK","May 17, 2026", listOf(3,13,24,27,41,44), true),
        DrawResult(17426,"13:25 MSK","May 17, 2026", listOf(19,24,29,34,36,43), true),
        DrawResult(17425,"11:30 MSK","May 17, 2026", listOf(1,5,6,21,31,40), true),
        DrawResult(17424,"10:00 MSK","May 17, 2026", listOf(5,8,19,23,28,37), true),
        DrawResult(17423,"22:59 MSK","May 16, 2026", listOf(2,3,25,26,35,39), false),
    )

    fun fetchResults(): List<DrawResult> {
        return try {
            val request = Request.Builder()
                .url("https://gosloto.co.za/6x45/results")
                .header("User-Agent","Mozilla/5.0 (Android 14)")
                .build()
            val resp = client.newCall(request).execute()
            val html = resp.body?.string() ?: return FALLBACK_DRAWS
            val doc = Jsoup.parse(html)
            val results = mutableListOf<DrawResult>()

            // Try multiple selectors
            val rows = doc.select(".result-row, .draw-result, tr.result, .result")
            for (row in rows) {
                val nums = row.select(".ball, .number, .num, td")
                    .mapNotNull { it.text().trim().toIntOrNull() }
                    .filter { it in 1..45 }
                if (nums.size == 6) {
                    val timeText = row.select(".time, .draw-time").firstOrNull()?.text() ?: "00:00 MSK"
                    val dateText = row.select(".date, .draw-date").firstOrNull()?.text() ?: "2026"
                    val drawNum = row.select(".draw-num, .draw-number").firstOrNull()
                        ?.text()?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                    val isMorning = try { timeText.split(":")[0].toInt() < 14 } catch(e: Exception) { false }
                    results.add(DrawResult(drawNum, timeText, dateText, nums.sorted(), isMorning))
                }
                if (results.size >= 20) break
            }
            if (results.size >= 5) results else FALLBACK_DRAWS
        } catch (e: Exception) {
            Log.e("DataService", "Fetch failed: ${e.message}")
            FALLBACK_DRAWS
        }
    }

    fun fetchPredictions(): Pair<List<Int>, List<Int>> {
        return try {
            val request = Request.Builder()
                .url("https://gosloto6x45.com")
                .header("User-Agent","Mozilla/5.0 (Android 14)")
                .build()
            val resp = client.newCall(request).execute()
            val html = resp.body?.string() ?: return getFallbackPredictions()
            val doc = Jsoup.parse(html)

            val morning = doc.select(".morning .ball, .morning-pred .num, [data-session=morning] .ball")
                .mapNotNull { it.text().trim().toIntOrNull() }.filter { it in 1..45 }
            val evening = doc.select(".evening .ball, .evening-pred .num, [data-session=evening] .ball")
                .mapNotNull { it.text().trim().toIntOrNull() }.filter { it in 1..45 }

            if (morning.size == 6 && evening.size == 6)
                Pair(morning, evening)
            else
                getFallbackPredictions()
        } catch (e: Exception) {
            getFallbackPredictions()
        }
    }

    fun computeStats(draws: List<DrawResult>): NumberStats {
        val freq = (1..45).associateWith { 0 }.toMutableMap()
        val lastSeen = (1..45).associateWith { 9999 }.toMutableMap()
        draws.forEachIndexed { idx, draw ->
            draw.numbers.forEach { n ->
                freq[n] = (freq[n] ?: 0) + 1
                if (lastSeen[n] == 9999) lastSeen[n] = idx
            }
        }
        val sorted = freq.entries.sortedByDescending { it.value }
        val hot = sorted.take(10).map { it.key }
        val cold = sorted.takeLast(10).map { it.key }
        val overdue = lastSeen.entries.sortedByDescending { it.value }.take(10).map { it.key }
        return NumberStats(hot, cold, overdue, freq)
    }

    private fun getFallbackPredictions() = Pair(
        listOf(9, 18, 22, 34, 38, 45),
        listOf(7, 15, 27, 31, 34, 43)
    )
}
