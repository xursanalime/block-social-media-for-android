package app.reelsblock

import android.content.SharedPreferences
import java.time.LocalDate

/**
 * Bloklanadigan bo'lim.
 *
 * [viewIds] bo'sh bo'lmasa — faqat shu view'lar ekranda ko'ringanda «Orqaga»
 * bosiladi (ilovaning qolgan qismi ishlayveradi). Instagram/YouTube yangilanib
 * ID o'zgarsa, faqat shu faylni tahrirlash kifoya.
 *
 * [viewIds] bo'sh bo'lsa — ilova butunlay bloklanadi: ochilishi bilan bosh
 * ekranga chiqarib yuboriladi.
 *
 * Yangi paket qo'shilsa, res/xml/blocker_service.xml dagi
 * android:packageNames ro'yxatiga ham qo'shish kerak.
 */
class BlockTarget(
    val prefKey: String,
    val packageNames: List<String>,
    val viewIds: List<String> = emptyList(),
    val defaultEnabled: Boolean = true,
) {
    val wholeApp get() = viewIds.isEmpty()
}

object Targets {
    const val PREFS = "settings"

    val INSTAGRAM_REELS = BlockTarget(
        prefKey = "block_instagram_reels",
        packageNames = listOf("com.instagram.android"),
        viewIds = listOf(
            "clips_viewer_view_pager",
            "clips_viewer_container",
            "clips_swipe_refresh_container",
        ),
    )

    val YOUTUBE_SHORTS = BlockTarget(
        prefKey = "block_youtube_shorts",
        packageNames = listOf("com.google.android.youtube"),
        viewIds = listOf(
            "reel_recycler",
            "reel_player_page_container",
            "reel_watch_fragment_root",
        ),
    )

    val TIKTOK = BlockTarget(
        prefKey = "block_tiktok",
        packageNames = listOf(
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "com.zhiliaoapp.musically.go",
        ),
        defaultEnabled = false,
    )

    val ALL = listOf(INSTAGRAM_REELS, YOUTUBE_SHORTS, TIKTOK)

    fun forPackage(packageName: CharSequence?): BlockTarget? =
        ALL.firstOrNull { t -> t.packageNames.any { it.contentEquals(packageName) } }
}

/** Vaqtinchalik pauza. */
object Pause {
    private const val KEY = "paused_until"

    /** Pauza tugaydigan vaqt (epoch ms) yoki pauza bo'lmasa 0. */
    fun until(prefs: SharedPreferences): Long =
        prefs.getLong(KEY, 0L).takeIf { it > System.currentTimeMillis() } ?: 0L

    fun start(prefs: SharedPreferences, minutes: Int) =
        prefs.edit().putLong(KEY, System.currentTimeMillis() + minutes * 60_000L).apply()

    fun stop(prefs: SharedPreferences) = prefs.edit().remove(KEY).apply()
}

/** Necha marta bloklangani: bugun va jami. */
object Stats {
    private const val DAY = "stats_day"
    private const val TODAY = "stats_today"
    private const val TOTAL = "stats_total"

    fun today(prefs: SharedPreferences): Int =
        if (prefs.getLong(DAY, -1) == LocalDate.now().toEpochDay()) prefs.getInt(TODAY, 0) else 0

    fun total(prefs: SharedPreferences): Int = prefs.getInt(TOTAL, 0)

    fun increment(prefs: SharedPreferences) {
        prefs.edit()
            .putLong(DAY, LocalDate.now().toEpochDay())
            .putInt(TODAY, today(prefs) + 1)
            .putInt(TOTAL, total(prefs) + 1)
            .apply()
    }
}
