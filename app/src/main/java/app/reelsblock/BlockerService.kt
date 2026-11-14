package app.reelsblock

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

/**
 * Instagram yoki YouTube oynasida Reels/Shorts pleyeri ko'rinsa,
 * "Orqaga" tugmasini bosib undan chiqarib yuboradi.
 */
class BlockerService : AccessibilityService() {

    private val prefs by lazy { getSharedPreferences(Targets.PREFS, Context.MODE_PRIVATE) }
    private var lastBackAt = 0L
    private var lastToastAt = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val target = Targets.forPackage(event?.packageName) ?: return
        if (!prefs.getBoolean(target.prefKey, true)) return

        // "Orqaga" bosilgach ekran almashib ulgurishi uchun kutamiz, aks holda
        // ketma-ket bosilib ilovaning o'zidan chiqib ketishi mumkin.
        val now = SystemClock.uptimeMillis()
        if (now - lastBackAt < BACK_COOLDOWN_MS) return

        val root = rootInActiveWindow ?: return
        if (!target.packageName.contentEquals(root.packageName)) return

        val blockedVisible = target.viewIds.any { id ->
            root.findAccessibilityNodeInfosByViewId("${target.packageName}:id/$id")
                .any { it.isVisibleToUser }
        }
        if (!blockedVisible) return

        lastBackAt = now
        performGlobalAction(GLOBAL_ACTION_BACK)

        if (now - lastToastAt > TOAST_COOLDOWN_MS) {
            lastToastAt = now
            Toast.makeText(this, R.string.blocked_toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInterrupt() = Unit

    private companion object {
        const val BACK_COOLDOWN_MS = 1000L
        const val TOAST_COOLDOWN_MS = 4000L
    }
}
