package com.unistmo.tygersys.ui.slideshow

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.unistmo.tygersys.R

class SlideshowFragment : Fragment() {
    var lblLinkTY: TextView ?= null
    var imgTY: ImageView ?=null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=MyGPiqaPvGM")))
        imgTY=root.findViewById(R.id.imgTY) as ImageView
        lblLinkTY=root.findViewById(R.id.lblLinkVideo) as TextView
        imgTY?.setOnClickListener{
            startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=MyGPiqaPvGM")))
        }
        lblLinkTY?.setOnClickListener {
            startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=MyGPiqaPvGM")))
        }
        return root
    }
}