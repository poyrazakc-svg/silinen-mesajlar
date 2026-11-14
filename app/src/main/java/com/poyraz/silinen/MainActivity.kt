package com.poyraz.silinen

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var list: LinearLayout
    private lateinit var onlyDeleted: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(16), px(32), px(16), px(16))
        }

        root.addView(TextView(this).apply {
            text = "Silinen Mesajlar"
            textSize = 24f
            setTextColor(Color.BLACK)
        })

        status = TextView(this).apply {
            textSize = 14f
            setPadding(0, px(8), 0, px(8))
        }
        root.addView(status)

        root.addView(Button(this).apply {
            text = "Bildirim erişimini aç / kapat"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        })

        onlyDeleted = CheckBox(this).apply {
            text = "Sadece silinenleri göster"
            isChecked = true
            setOnCheckedChangeListener { _, _ -> refresh() }
        }
        root.addView(onlyDeleted)

        root.addView(Button(this).apply {
            text = "Listeyi temizle"
            setOnClickListener { Store.clear(this@MainActivity); refresh() }
        })

        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val scroll = ScrollView(this).apply {
            addView(list, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun hasAccess(): Boolean {
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: return false
        val me = ComponentName(this, NotifService::class.java).flattenToString()
        return flat.split(":").contains(me)
    }

    private fun refresh() {
        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()

        if (hasAccess()) {
            status.text = "✅ Bildirim erişimi açık. WhatsApp bildirimleri kaydediliyor."
            status.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            status.text = "⚠️ Bildirim erişimi kapalı. Aşağıdaki butondan bu uygulamaya izin ver."
            status.setTextColor(Color.parseColor("#C62828"))
        }

        list.removeAllViews()
        val fmt = SimpleDateFormat("dd.MM HH:mm", Locale("tr"))
        val msgs = Store.all(this).filter { !onlyDeleted.isChecked || it.deleted }

        if (msgs.isEmpty()) {
            list.addView(TextView(this).apply {
                text = if (onlyDeleted.isChecked) "Henüz silinen mesaj yok." else "Henüz mesaj yok."
                setPadding(0, px(16), 0, 0)
            })
            return
        }

        for (m in msgs) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(px(12), px(10), px(12), px(10))
                setBackgroundColor(if (m.deleted) Color.parseColor("#FFEBEE") else Color.parseColor("#F5F5F5"))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = px(8) }
            }
            card.addView(TextView(this).apply {
                text = (if (m.deleted) "🚫 " else "") + m.sender + "  ·  " + fmt.format(Date(m.time))
                textSize = 12f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.START
            })
            card.addView(TextView(this).apply {
                text = m.text
                textSize = 16f
                setTextColor(Color.BLACK)
                setTextIsSelectable(true)
            })
            list.addView(card)
        }
    }
}
