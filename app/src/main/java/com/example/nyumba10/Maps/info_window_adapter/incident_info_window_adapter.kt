package com.example.nyumba10.Maps.info_window_adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


public class incident_info_window_adapter : GoogleMap.InfoWindowAdapter {

private lateinit var view: View
    private lateinit var mContext: Context

    fun incident_info_window_adapter(context: Context) {
        mContext = context
    }




   /* private fun rendowWindowText(
        marker: Marker,
        view: View
    ) {
        val title = marker.title
        val tvTitle = view.findViewById<View>(R.id.title) as TextView
        if (title != "") {
            tvTitle.text = title
        }
        val snippet = marker.snippet
        val tvSnippet = view.findViewById<View>(R.id) as TextView
        if (snippet != "") {
            tvSnippet.text = snippet
        }
    }*/
    override fun getInfoContents(info_contents_marker: Marker?): View {
return view
   }

    override fun getInfoWindow(info_window_marker: Marker?): View {

        return  view
    }
}