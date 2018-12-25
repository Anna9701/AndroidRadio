package com.annwy.radio.radioStations

import android.content.res.Resources
import android.content.res.XmlResourceParser
import com.annwy.radio.R
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.ArrayList

class RadioStationsContent(private val resources: Resources?, private val regionName: String) {
    val items: MutableList<RadioStation> = ArrayList()
    private val namespace: String? = null

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
    private fun readStations(parser: XmlResourceParser): List<RadioStation> {
        val entries = mutableListOf<RadioStation>()
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
    private fun readRadioStation(parser: XmlResourceParser): RadioStation {
        parser.require(XmlResourceParser.START_TAG, namespace, "radioStation")
        var name = String()
        var url = String()
        var region = String()
        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.eventType != XmlResourceParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "name" -> name = readTag(parser, "name")
                "region" -> region = readTag(parser, "region")
                "url" -> url = readTag(parser, "url")
                else -> skip(parser)
            }
        }
        return RadioStation(name, url, region)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTag(parser: XmlResourceParser, tag: String): String {
        parser.require(XmlResourceParser.START_TAG, namespace, tag)
        val summary = readText(parser)
        parser.require(XmlResourceParser.END_TAG, namespace, tag)
        return summary
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlResourceParser): String {
        var result = ""
        if (parser.next() == XmlResourceParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlResourceParser) {
        if (parser.eventType != XmlResourceParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlResourceParser.END_TAG -> --depth
                XmlResourceParser.START_TAG -> ++depth
            }
        }
    }

    data class RadioStation(val radioName: String, val radioUrl: String, val regionName: String) {
        override fun toString(): String = "$radioName - $regionName"
    }
}
