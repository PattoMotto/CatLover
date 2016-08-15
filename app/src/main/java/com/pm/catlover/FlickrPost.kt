package com.pm.catlover

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class FlickrPost : Serializable{
    // https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
    var farmId: Int = 0
    var serverId: String? = null
    var id: String? = null
    var secret: String? = null
    var title: String? = null
    companion object {
        fun newInstance(id: String, title: String, farmId: Int,serverId: String, secret: String) : FlickrPost {
            val instance = FlickrPost()
            instance.id = id
            instance.title = title
            instance.farmId = farmId
            instance.serverId = serverId
            instance.secret = secret
            return instance
        }
        fun newInstance(jsonObject: JSONObject) : FlickrPost {
            val instance = FlickrPost()
            instance.id = jsonObject.getString("id")
            instance.title = jsonObject.getString("title")
            instance.farmId = jsonObject.getInt("farmId")
            instance.serverId = jsonObject.getString("serverId")
            instance.secret = jsonObject.getString("secret")
            return instance
        }
    }
    private fun getUrl(size: String) : String {
        return "https://farm$farmId.staticflickr.com/$serverId/$id"+"_"+secret+"_$size.jpg"
    }
    fun getUrlBig() : String {
        return getUrl("b")
    }
    fun getUrlMedium() : String {
        return getUrl("z")
    }
    fun getUrlThumbnail() : String {
        return getUrl("t")
    }
    fun toJson() : JSONObject{
        var jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("title", title)
        jsonObject.put("farmId", farmId)
        jsonObject.put("serverId", serverId)
        jsonObject.put("secret", secret)
        return jsonObject
    }
    fun savePost(context: Context) : Boolean{
        val sharedPref = getSharedPref(context)
        val savedListKey = getSavedListKey(context)
        if (sharedPref.contains(savedListKey)){
            val raw = sharedPref.getString(savedListKey, "")
            var jsonArray = JSONArray(raw)
            if (firstInSavedList(jsonArray) < 0) {
                jsonArray.put(toJson())
                saveToSharedPref(jsonArray, savedListKey, sharedPref)
            } else {
                return false
            }
        } else {
            var jsonArray = JSONArray()
            jsonArray.put(toJson())
            saveToSharedPref(jsonArray, savedListKey, sharedPref)
        }
        return true
    }
    fun removeSavePost(context: Context) : Boolean {
        val sharedPref = getSharedPref(context)
        val savedListKey = getSavedListKey(context)
        if (sharedPref.contains(savedListKey)){
            val raw = sharedPref.getString(savedListKey, "")
            var jsonArray = JSONArray(raw)
            val index = firstInSavedList(jsonArray)
            if (index >= 0) {
                jsonArray.remove(index)
                saveToSharedPref(jsonArray, savedListKey, sharedPref)
            } else {
                return false
            }
        } else {
            return false
        }
        return true
    }

    private fun getSavedListKey(context: Context): String? {
        val savedListKey = context.getString(R.string.savedList)
        return savedListKey
    }

    private fun getSharedPref(context: Context): SharedPreferences{
        val sharedPrefKey = context.getString(R.string.sharedPref)
        val sharedPref = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return sharedPref
    }

    private fun firstInSavedList(jsonArray: JSONArray): Int {
        for (i in 0..(jsonArray.length() - 1)) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (id.equals(jsonObject.getString("id")) && farmId.equals(jsonObject.getInt("farmId")) && serverId.equals(jsonObject.getString("serverId"))) {
                return i
            }
        }
        return -1
    }

    private fun saveToSharedPref(jsonArray: JSONArray, savedListKey: String?, sharedPref: SharedPreferences) {
        val editor = sharedPref.edit()
        editor.putString(savedListKey, jsonArray.toString())
        editor.commit()
    }
}