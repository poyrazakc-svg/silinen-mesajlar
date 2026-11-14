package com.poyraz.silinen

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Msg(val sender: String, val text: String, val time: Long, val deleted: Boolean)

object Store {
    private const val PREF = "msgs"
    private const val KEY = "list"
    private const val MAX = 2000

    private val deletedMarkers = listOf(
        "bu mesaj silindi",
        "this message was deleted",
        "mesaj silindi"
    )

    fun isDeletedMarker(text: String): Boolean {
        val t = text.lowercase()
        return deletedMarkers.any { t.contains(it) }
    }

    @Synchronized
    fun load(ctx: Context): JSONArray {
        val s = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "[]")
        return try { JSONArray(s) } catch (e: Exception) { JSONArray() }
    }

    @Synchronized
    private fun save(ctx: Context, arr: JSONArray) {
        var a = arr
        if (a.length() > MAX) {
            val trimmed = JSONArray()
            for (i in a.length() - MAX until a.length()) trimmed.put(a.get(i))
            a = trimmed
        }
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, a.toString()).apply()
    }

    @Synchronized
    fun handle(ctx: Context, sender: String, text: String, time: Long) {
        val arr = load(ctx)
        if (isDeletedMarker(text)) {
            // Aynı kişiden gelen son silinmemiş mesajı "silindi" olarak işaretle
            for (i in arr.length() - 1 downTo 0) {
                val o = arr.getJSONObject(i)
                if (o.getString("sender") == sender && !o.getBoolean("deleted")) {
                    o.put("deleted", true)
                    break
                }
            }
            save(ctx, arr)
            return
        }
        // Aynı bildirim tekrar gelirse ikinci kez ekleme
        for (i in arr.length() - 1 downTo maxOf(0, arr.length() - 15)) {
            val o = arr.getJSONObject(i)
            if (o.getString("sender") == sender && o.getString("text") == text &&
                Math.abs(o.getLong("time") - time) < 60_000
            ) return
        }
        arr.put(JSONObject().apply {
            put("sender", sender)
            put("text", text)
            put("time", time)
            put("deleted", false)
        })
        save(ctx, arr)
    }

    fun all(ctx: Context): List<Msg> {
        val arr = load(ctx)
        val out = ArrayList<Msg>()
        for (i in arr.length() - 1 downTo 0) {
            val o = arr.getJSONObject(i)
            out.add(Msg(o.getString("sender"), o.getString("text"), o.getLong("time"), o.getBoolean("deleted")))
        }
        return out
    }

    fun clear(ctx: Context) = save(ctx, JSONArray())
}
