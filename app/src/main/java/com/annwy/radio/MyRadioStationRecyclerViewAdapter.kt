package com.annwy.radio

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.annwy.radio.RadioStationFragment.OnListFragmentInteractionListener
import com.annwy.radio.radioStations.RadioStationsContent.RadioStation

import kotlinx.android.synthetic.main.fragment_radiostation.view.*

class MyRadioStationRecyclerViewAdapter(private val mValues: List<RadioStation>,
                                        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyRadioStationRecyclerViewAdapter.ViewHolder>() {
    private var stationIndex = 0
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
        holder.mIdView.text = (++stationIndex).toString()
        holder.mContentView.text = item.radioName

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
