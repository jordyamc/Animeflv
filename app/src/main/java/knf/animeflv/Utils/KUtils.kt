package knf.animeflv.Utils

import android.content.Context
import knf.animeflv.Cloudflare.BypassHolder
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

fun regexFindFirst(regex: String, text: String): String? {
    return regex.toRegex().find(text)?.value
}

operator fun JSONArray.iterator(): Iterator<JSONObject> = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

fun forEachJsonArray(array: JSONArray, callback: (json: JSONObject) -> Unit) {
    for (json in array) {
        callback.invoke(json)
    }
}

fun executeOkHttpCookies(context: Context, url: String, method: String = "GET"): Response = Request.Builder().apply {
    url(url)
    method(method, if (method == "POST") RequestBody.create(MediaType.get("text/plain"), "") else null)
    header("User-Agent", BypassHolder.getUserAgent())
    header("Cookie", BypassHolder.getBasicCookieString(context))
}.build().execute()

fun Request.execute(): Response {
    return OkHttpClient().newBuilder().build().newCall(this).execute()
}

fun extractLink(html: String): String {
    val matcher = Pattern.compile("https?://[a-zA-Z0-a.=?/&\\-]+").matcher(html)
    matcher.find()
    return matcher.group(0)
}

fun getYUvideoLink(link: String): String {
    val pattern = Pattern.compile("file: ?'(.*vidcache.*mp4)'")
    val matcher = pattern.matcher(link)
    matcher.find()
    return matcher.group(1)
}