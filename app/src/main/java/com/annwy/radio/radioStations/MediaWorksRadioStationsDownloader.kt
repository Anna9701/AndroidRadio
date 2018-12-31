package com.annwy.radio.radioStations

import android.content.res.Resources
import android.content.res.XmlResourceParser
import com.annwy.radio.R
import com.annwy.radio.models.mediaWorksApi.RadioStation
import com.annwy.radio.utils.XmlParser
import com.google.gson.Gson
import khttp.get

class MediaWorksRadioStationsDownloader(resources: Resources?, private val regionName: String? = null) :
    XmlParser(resources) {
    var radioStations = ArrayList<RadioStation>()
    private val headers = mapOf("content-type" to "application/json")
    private val jsonConverter = Gson()

    init {
        val apisUrls = getRadioApiUrlFromResources(resources!!.getXml(R.xml.new_zealand_media_works_api_stations))
        for (apiUrl in apisUrls) {
            val stations = getRadioStations(apiUrl)
            radioStations.addAll(stations)
        }
    }

    private fun getRadioApiUrlFromResources(parser: XmlResourceParser): List<String> {
        val entries = mutableListOf<String>()
        parser.next(); parser.next()
        parser.require(XmlResourceParser.START_TAG, namespace, "media_work_net_apis")
        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.eventType != XmlResourceParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "api") {
                entries.add(readTag(parser, "api"))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    private fun getRadioStation(url: String): RadioStation? {
        val apiResponse = get(
            url = url,
            headers = headers
        )
        return jsonConverter.fromJson(apiResponse.text, RadioStation::class.java)
    }

    private fun getRadioStations(url: String): ArrayList<RadioStation> {
        val radioStations = ArrayList<RadioStation>()
        val radioStation = getRadioStation(url) ?: return radioStations
        if (radioStation.regionList == null) {
            radioStations.add(radioStation)
        } else if (radioStation.regionList.isNotEmpty()) {
            if (regionName != null) {
                radioStation.regionList.removeAll { station ->
                    !regionName.equals(
                        station.regionName,
                        ignoreCase = true
                    )
                }
            }
            for (station in radioStation.regionList) {
                radioStations.addAll(getRadioStations(station.radioUrl))
            }
        }
        return radioStations
    }
}