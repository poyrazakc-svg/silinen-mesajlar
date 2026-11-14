package com.poyraz.silinen

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotifService : NotificationListenerService() {

    private val summaryRegex = Regex("\\d+\\s+(yeni\\s+)?(mesaj|messages?)", RegexOption.IGNORE_CASE)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") return
        val n = sbn.notification
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val extras = n.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        if (title == "WhatsApp") return
        if (summaryRegex.containsMatchIn(text) && !Store.isDeletedMarker(text)) return

        Store.handle(applicationContext, title, text, sbn.postTime)
    }
}
