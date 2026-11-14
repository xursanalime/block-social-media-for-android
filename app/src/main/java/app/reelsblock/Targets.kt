package app.reelsblock

/**
 * Bloklanadigan bo'lim: ilova paketi va shu bo'lim ekranda ochiqligini
 * bildiradigan view ID'lar. Instagram/YouTube yangilanib ID o'zgarsa,
 * faqat shu faylni tahrirlash kifoya.
 *
 * Yangi paket qo'shilsa, res/xml/blocker_service.xml dagi
 * android:packageNames ro'yxatiga ham qo'shish kerak.
 */
class BlockTarget(
    val prefKey: String,
    val packageName: String,
    val viewIds: List<String>,
)

object Targets {
    const val PREFS = "settings"

    val INSTAGRAM_REELS = BlockTarget(
        prefKey = "block_instagram_reels",
        packageName = "com.instagram.android",
        viewIds = listOf(
            "clips_viewer_view_pager",
            "clips_viewer_container",
            "clips_swipe_refresh_container",
        ),
    )

    val YOUTUBE_SHORTS = BlockTarget(
        prefKey = "block_youtube_shorts",
        packageName = "com.google.android.youtube",
        viewIds = listOf(
            "reel_recycler",
            "reel_player_page_container",
            "reel_watch_fragment_root",
        ),
    )

    val ALL = listOf(INSTAGRAM_REELS, YOUTUBE_SHORTS)

    fun forPackage(packageName: CharSequence?): BlockTarget? =
        ALL.firstOrNull { it.packageName.contentEquals(packageName) }
}
