package com.annwy.radio

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.annwy.radio.models.IRadioStation
import com.annwy.radio.radioStations.MediaWorksRadioStationsDownloader

import com.annwy.radio.radioStations.XmlRadioStationsDownloader
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.concurrent.Future

class RadioStationFragment : Fragment() {
    private var listener: OnListFragmentInteractionListener? = null
    private var regionName = String()
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            regionName = it.getString(RADIO_STATION_REGION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_radiostation_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val radioStations = ArrayList<IRadioStation>()
                adapter = MyRadioStationRecyclerViewAdapter(radioStations, listener)
                getXmlRadioStations(radioStations)
                getMediaWorkNetApiRadioStations(radioStations)
            }
        }
        return view
    }

    private fun RecyclerView.getXmlRadioStations(radioStations: ArrayList<IRadioStation>) {
        doAsync {
            radioStations.addAll(XmlRadioStationsDownloader(activity?.resources, regionName).items)
            uiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun RecyclerView.getMediaWorkNetApiRadioStations(radioStations: ArrayList<IRadioStation>): Future<Unit> {
        return doAsync {
            val mediaWorksRadioStations = MediaWorksRadioStationsDownloader(resources, regionName).radioStations
            radioStations.addAll(mediaWorksRadioStations)
            uiThread {
                adapter.notifyDataSetChanged()
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: IRadioStation?)
    }

    companion object {
        const val RADIO_STATION_REGION = "com.radio.annwy.radio.station_region"

        @JvmStatic
        fun newInstance(regionName: String) =
            RadioStationFragment().apply {
                arguments = Bundle().apply {
                    putString(RADIO_STATION_REGION, regionName)
                }
            }
    }
}
