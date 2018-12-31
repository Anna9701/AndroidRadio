package com.annwy.radio.radioStations

import com.annwy.radio.models.mediaWorksApi.RadioStation
import com.google.gson.Gson
import khttp.get

class MediaWorksRadioStationsDownloader(private val regionName: String? = null) {
    var radioStations = ArrayList<RadioStation>()
    private val headers = mapOf("content-type" to "application/json")
    private val jsonConverter = Gson()

    init {
        val breezeStations = getRadioStations("https://radio-api.mediaworks.nz/radio-api/v3/station/thebreeze/web")
        radioStations.addAll(breezeStations)
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