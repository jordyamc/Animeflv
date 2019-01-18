package knf.animeflv.Utils

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

fun extractLink(html: String): String {
    val matcher = Pattern.compile("https?://[a-zA-Z0-a.=?/&\\-]+").matcher(html)
    matcher.find()
    return matcher.group(0)
}