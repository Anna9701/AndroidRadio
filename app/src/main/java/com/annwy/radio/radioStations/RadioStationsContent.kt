package com.annwy.radio.radioStations

import java.util.ArrayList
import java.util.HashMap

object RadioStationsContent {
    val ITEMS: MutableList<RadioStation> = ArrayList()
    private val ITEM_MAP: MutableMap<String, RadioStation> = HashMap()

    private const val radioUrl = "https://livestream.mediaworks.nz/radio_origin/breeze_128kbps/chunklist.m3u8"
    private const val radioLabel = "The Breeze - Auckland"

    init {
        addItem(RadioStation("1", radioLabel, radioUrl))
    }

    private fun addItem(item: RadioStation) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    data class RadioStation(val id: String, val radioName: String, val radioUrl: String) {
        override fun toString(): String = radioName
    }
}
