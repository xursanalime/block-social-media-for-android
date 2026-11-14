package app.reelsblock

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.open_settings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        bindSwitch(R.id.switch_instagram, Targets.INSTAGRAM_REELS)
        bindSwitch(R.id.switch_youtube, Targets.YOUTUBE_SHORTS)
    }

    override fun onResume() {
        super.onResume()
        val enabled = isServiceEnabled()
        findViewById<TextView>(R.id.status).apply {
            setText(if (enabled) R.string.status_on else R.string.status_off)
            setTextColor(getColor(if (enabled) R.color.status_on else R.color.status_off))
        }
    }

    private fun bindSwitch(id: Int, target: BlockTarget) {
        val prefs = getSharedPreferences(Targets.PREFS, Context.MODE_PRIVATE)
        findViewById<Switch>(id).apply {
            isChecked = prefs.getBoolean(target.prefKey, true)
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
