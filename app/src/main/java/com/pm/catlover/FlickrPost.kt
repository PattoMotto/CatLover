package com.pm.catlover

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
}