package com.pm.catlover

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide

class FeedAdapter : BaseAdapter {
    var flickrPostList: List<FlickrPost> = listOf()
    var context: Context
    constructor(context: Context, flickrPostList: List<FlickrPost>) :super(){
        this.flickrPostList = flickrPostList
        this.context = context
    }
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var feedView: View
        if (view != null) {
            feedView = view
        } else {
            var layoutInflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            feedView = layoutInflater.inflate(R.layout.feed_item,parent,false)
        }
        val feedItemImageView = feedView.findViewById(R.id.feedItemImageView) as ImageView
        val url = flickrPostList.get(position).getUrlMedium()
        Glide
                .with(context)
                .load(url)
                .crossFade()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(feedItemImageView)
        return feedView
    }

    override fun getCount(): Int {
        return flickrPostList.count()
    }

    override fun getItem(position: Int): Any {
        return flickrPostList.get(position)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }
}