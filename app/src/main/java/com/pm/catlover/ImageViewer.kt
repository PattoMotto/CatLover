package com.pm.catlover

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_include.*

class ImageViewer : AppCompatActivity() {
    var flickrPost: FlickrPost? = null
    var FLICKR_DATA:String = "flickrData"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        flickrPost = intent.getSerializableExtra(FLICKR_DATA) as FlickrPost?
        if (flickrPost == null) {
            finish()
        } else {
            imageViewerTextView.setText(flickrPost?.title)
            Glide
                    .with(this)
                    .load(flickrPost?.getUrlBig())
                    .crossFade()
                    .fitCenter()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imageViewerImageView)
        }
    }
}
