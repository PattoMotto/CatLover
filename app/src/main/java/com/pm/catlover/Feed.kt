package com.pm.catlover

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.json.JSONArray

class Feed : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var title: String? = null
    private var flickrPostArray: MutableList<FlickrPost> = mutableListOf()
    private var feedGridView: GridView? = null
    private var swipe_container: SwipeRefreshLayout? = null
    enum class Mode {
        NORMAL, LOVED
    }
    private var mode: Mode? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_feed, container, false)
        feedGridView = view?.findViewById(R.id.feedGridView) as GridView
        feedGridView?.adapter = FeedAdapter(context, flickrPostArray)
        feedGridView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val intent: Intent = Intent(context, ImageViewer::class.java).apply {
                putExtra("flickrData", flickrPostArray.get(i))
            }
            startActivity(intent)
        }
        feedGridView?.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            onTakeActionWithGrid(i)
            true
        }
        swipe_container = view?.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        swipe_container?.setOnRefreshListener(this)
        swipe_container?.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light)
        return view
    }

    private fun onTakeActionWithGrid(i: Int) {
        when (mode) {
            Mode.NORMAL -> {
                if (flickrPostArray.get(i).savePost(context = context)) {
                    Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.alreadySaved, Toast.LENGTH_SHORT).show()
                }
            }
            Mode.LOVED -> {
                if (flickrPostArray.get(i).removeSavePost(context = context)) {
                    Toast.makeText(context, R.string.removed, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.notFound, Toast.LENGTH_SHORT).show()
                }
                refresh()
            }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        refresh()
    }

    private fun refresh() {
        when (mode) {
            Mode.NORMAL -> loadFromApi()
            Mode.LOVED -> loadFromSavedList()
        }
    }

    fun loadFromSavedList(){
        val sharedPref = getSharedPref(context)
        val savedListKey = getSavedListKey(context)
        if (sharedPref.contains(savedListKey)) {
            val jsonArray = JSONArray(sharedPref.getString(savedListKey, ""))
            flickrPostArray.clear()
            for (i in 0..(jsonArray.length() - 1)) {
                flickrPostArray.add(FlickrPost.newInstance(jsonArray.getJSONObject(i)))
            }
            feedGridView?.adapter = FeedAdapter(getContext(), flickrPostArray)
        }
        swipe_container?.setRefreshing(false)

    }

    private fun getSavedListKey(context: Context): String? {
        val savedListKey = context.getString(R.string.savedList)
        return savedListKey
    }
    private fun getSharedPref(context: Context): SharedPreferences {
        val sharedPrefKey = context.getString(R.string.sharedPref)
        val sharedPref = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return sharedPref
    }
    fun loadFromApi(){
        swipe_container?.setRefreshing(true)
        val apiKey = getString(R.string.flickr_api_key)
        "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$apiKey&text=cat&sort=date-taken-desc&format=json&nojsoncallback=?".httpGet().responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    val error = result.get()
                    swipe_container?.setRefreshing(false)
                }
                is Result.Success -> {
                    swipe_container?.setRefreshing(false)
                    val data = result.get()
                    if (data.obj().getString("stat").equals("ok")) {
                        val photoArray = data.obj().getJSONObject("photos").getJSONArray("photo")
                        for (i in 0..(photoArray.length()-1)) {
                            val eachPhotoObject = photoArray.getJSONObject(i)
                            val id = eachPhotoObject.getString("id")
                            val secret = eachPhotoObject.getString("secret")
                            val server = eachPhotoObject.getString("server")
                            val farm = eachPhotoObject.getInt("farm")
                            val title = eachPhotoObject.getString("title")
                            flickrPostArray.add(FlickrPost.newInstance(id = id, title = title, secret = secret, farmId = farm, serverId = server))
                        }
                        feedGridView?.adapter = FeedAdapter(getContext(), flickrPostArray)

                    }
                }
            }
        }
    }

    override fun onRefresh() {
        refresh()
    }
    fun getTitle() : String?{
        return title
    }

    companion object {
        fun newInstance(mode: Mode): Feed {
            val fragment = Feed()
            fragment.mode = mode
            when (mode) {
                Mode.NORMAL -> fragment.title = "Feeds"
                Mode.LOVED -> fragment.title = "Loved"
            }
            return fragment
        }
    }
}
