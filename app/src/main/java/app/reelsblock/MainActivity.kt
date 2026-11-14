package app.reelsblock

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import java.util.Date

class MainActivity : Activity() {

    private val prefs by lazy { getSharedPreferences(Targets.PREFS, Context.MODE_PRIVATE) }
    private val handler = Handler(Looper.getMainLooper())
    private val refresh = Runnable { render() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.open_settings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        bindSwitch(R.id.switch_instagram, Targets.INSTAGRAM_REELS)
        bindSwitch(R.id.switch_youtube, Targets.YOUTUBE_SHORTS)
        bindSwitch(R.id.switch_tiktok, Targets.TIKTOK)

        findViewById<Button>(R.id.pause_5).setOnClickListener { Pause.start(prefs, 5); render() }
        findViewById<Button>(R.id.pause_15).setOnClickListener { Pause.start(prefs, 15); render() }
        findViewById<Button>(R.id.resume).setOnClickListener { Pause.stop(prefs); render() }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refresh)
    }

    private fun render() {
        val enabled = isServiceEnabled()
        val pausedUntil = Pause.until(prefs)
        val paused = enabled && pausedUntil != 0L

        findViewById<TextView>(R.id.status).apply {
            text = when {
                !enabled -> getString(R.string.status_off)
                paused -> getString(
                    R.string.status_paused,
                    DateFormat.getTimeFormat(context).format(Date(pausedUntil))
                )
                else -> getString(R.string.status_on)
            }
            setTextColor(
                getColor(
                    when {
                        !enabled -> R.color.status_off
                        paused -> R.color.status_paused
                        else -> R.color.status_on
                    }
                )
            )
        }

        // Xizmat yoqilgach sozlamalar tugmasi va yo'riqnoma kerak emas.
        val setup = if (enabled) View.GONE else View.VISIBLE
        findViewById<View>(R.id.open_settings).visibility = setup
        findViewById<View>(R.id.settings_hint).visibility = setup

        findViewById<View>(R.id.pause_row).visibility =
            if (enabled && !paused) View.VISIBLE else View.GONE
        findViewById<View>(R.id.resume).visibility = if (paused) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.stats).text =
            getString(R.string.stats, Stats.today(prefs), Stats.total(prefs))

        // Pauza tugaganda ekran o'zi yangilansin.
        handler.removeCallbacks(refresh)
        if (paused) handler.postDelayed(refresh, pausedUntil - System.currentTimeMillis() + 500)
    }

    private fun bindSwitch(id: Int, target: BlockTarget) {
        findViewById<Switch>(id).apply {
            isChecked = prefs.getBoolean(target.prefKey, target.defaultEnabled)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean(target.prefKey, checked).apply()
            }
        }
    }

    private fun isServiceEnabled(): Boolean {
        val me = ComponentName(this, BlockerService::class.java)
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { ComponentName.unflattenFromString(it) == me }
    }
}
