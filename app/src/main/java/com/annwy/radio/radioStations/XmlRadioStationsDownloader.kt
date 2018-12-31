package com.annwy.radio.radioStations

import android.content.res.Resources
import android.content.res.XmlResourceParser
import com.annwy.radio.models.XmlRadioStation
import com.annwy.radio.R
import com.annwy.radio.models.IRadioStation
import com.annwy.radio.utils.XmlParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.ArrayList

class XmlRadioStationsDownloader(resources: Resources?, private val regionName: String) : XmlParser(resources) {
    val items: MutableList<IRadioStation> = ArrayList()

    init {
        loadFromResources()
    }

    private fun loadFromResources() {
        val xml = resources?.getXml(R.xml.new_zealand_stations)
        val stations = readStations(xml!!)
        for (station in stations) {
            if (station.regionName.isEmpty() || station.regionName.equals(regionName, ignoreCase = true)) {
                items.add(station)
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readStations(parser: XmlResourceParser): List<IRadioStation> {
        val entries = mutableListOf<XmlRadioStation>()
        parser.next(); parser.next()
        parser.require(XmlResourceParser.START_TAG, namespace, "stations")
        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.eventType != XmlResourceParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "radioStation") {
                entries.add(readRadioStation(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRadioStation(parser: XmlResourceParser): XmlRadioStation {
        parser.require(XmlResourceParser.START_TAG, namespace, "radioStation")
        var name = String()
        var url = String()
        var region = String()
        var logoUrl = String()
        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.eventType != XmlResourceParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "name" -> name = readTag(parser, "name")
                "region" -> region = readTag(parser, "region")
                "url" -> url = readTag(parser, "url")
                "logoUrl" -> logoUrl = readTag(parser, "logoUrl")
                else -> skip(parser)
            }
        }
        return XmlRadioStation(name, url, region, logoUrl)
    }

}
