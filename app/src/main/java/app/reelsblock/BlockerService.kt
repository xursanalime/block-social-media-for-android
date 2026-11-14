package app.reelsblock

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

/**
 * Instagram yoki YouTube oynasida Reels/Shorts pleyeri ko'rinsa «Orqaga»
 * bosib undan chiqaradi; butunlay bloklangan ilovalarni bosh ekranga qaytaradi.
 */
class BlockerService : AccessibilityService() {

    private val prefs by lazy { getSharedPreferences(Targets.PREFS, Context.MODE_PRIVATE) }
    private var lastActionAt = 0L
    private var lastToastAt = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val target = Targets.forPackage(event?.packageName) ?: return
        if (!prefs.getBoolean(target.prefKey, target.defaultEnabled)) return
        if (Pause.until(prefs) != 0L) return

        // Harakatdan keyin ekran almashib ulgurishi uchun kutamiz, aks holda
        // «Orqaga» ketma-ket bosilib ilovaning o'zidan chiqib ketishi mumkin.
        val now = SystemClock.uptimeMillis()
        if (now - lastActionAt < ACTION_COOLDOWN_MS) return

        val root = rootInActiveWindow ?: return
        val pkg = root.packageName?.toString() ?: return
        if (pkg !in target.packageNames) return

        if (target.wholeApp) {
            performGlobalAction(GLOBAL_ACTION_HOME)
        } else {
            val blockedVisible = target.viewIds.any { id ->
                root.findAccessibilityNodeInfosByViewId("$pkg:id/$id").any { it.isVisibleToUser }
            }
            if (!blockedVisible) return
            performGlobalAction(GLOBAL_ACTION_BACK)
        }

        lastActionAt = now
        Stats.increment(prefs)

        if (now - lastToastAt > TOAST_COOLDOWN_MS) {
            lastToastAt = now
            Toast.makeText(
                this, getString(R.string.blocked_toast, Stats.today(prefs)), Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onInterrupt() = Unit

    private companion object {
        const val ACTION_COOLDOWN_MS = 1000L
        const val TOAST_COOLDOWN_MS = 4000L
    }
}
