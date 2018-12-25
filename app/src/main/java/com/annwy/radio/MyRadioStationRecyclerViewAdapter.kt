package com.annwy.radio

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.annwy.radio.RadioStationFragment.OnListFragmentInteractionListener
import com.annwy.radio.radioStations.RadioStationsContent.RadioStation
import kotlinx.android.synthetic.main.fragment_radiostation.view.*
import android.graphics.Bitmap
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MyRadioStationRecyclerViewAdapter(
    private val mValues: List<RadioStation>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyRadioStationRecyclerViewAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RadioStation
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_radiostation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        doAsync {
            var bitmap = loadImage("https://cdn.mediaworks.nz/thebreeze_timaru/Content/shows/images/1545272409483_brz_logo_1100x620.png",
                BitmapFactory.Options()
            )
            uiThread {
                holder.mImageView.setImageBitmap(bitmap)
            }
        }
        holder.mContentView.text = item.radioName

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    private fun loadImage(url: String, options: BitmapFactory.Options): Bitmap? {
        var bitmap: Bitmap? = null
        //val inputStream: InputStream? = null
        try {
            //  inputStream = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(openHttpsConnection(url), null, options)
            //  inputStream.close();

        } catch (ex: IOException) {
            Log.e("RecyclerView", ex.toString())
        }

        return bitmap
    }


    @Throws(IOException::class)
    private fun openHttpsConnection(strURL: String): InputStream? {
        var inputStream: InputStream? = null
        val url = URL(strURL)
        val conn = url.openConnection()
        try {
            val httpsConn = conn as HttpsURLConnection
            httpsConn.requestMethod = "GET"
            httpsConn.connect()

            if (httpsConn.responseCode == HttpsURLConnection.HTTP_OK) {
                inputStream = httpsConn.inputStream
            }
        } catch (ex: Exception) {
            Log.e("RecyclerView", ex.toString())
        }

        return inputStream
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mImageView: ImageView = mView.imageView2
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
