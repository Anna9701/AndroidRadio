package com.annwy.radio.radioStations

import com.annwy.radio.models.mediaWorksApi.RadioStation
import com.beust.klaxon.Klaxon
import khttp.get
import org.jetbrains.anko.doAsync

class MediaWorksRadioStationsDownloader(private val regionName: String? = null) {
    var radioStations = ArrayList<RadioStation>()
    private val headers = mapOf("content-type" to "application/json")

    init {
        doAsync {
            val breezeStations = getRadioStations("https://radio-api.mediaworks.nz/radio-api/v3/station/thebreeze/web")
            radioStations.addAll(breezeStations)
        }
    }

    private fun getRadioStation(url: String): RadioStation? {
        val apiResponse = get(
            url = url,
            headers = headers
        )
        return Klaxon().parse<RadioStation>(apiResponse.text)
    }

    private fun getRadioStations(url: String): ArrayList<RadioStation> {
        val radioStations = ArrayList<RadioStation>()
        val radioStation = getRadioStation(url) ?: return radioStations
        if (radioStation.regionList == null) {
            radioStations.add(radioStation)
        } else if (radioStation.regionList.isNotEmpty()) {
            for (station in radioStation.regionList) {
                if (regionName == null || regionName.equals(station.regionName, ignoreCase = true)) {
                    radioStations.addAll(getRadioStations(station.radioUrl))
                }
            }
        }
        return radioStations
    }
}