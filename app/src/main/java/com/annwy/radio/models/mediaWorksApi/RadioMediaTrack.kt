package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable

data class RadioMediaTrack(
    val type: String,
    val name: String,
    val artist: String,
    val played_date: String,
    val played_time: String,
    val length_in_secs: String,
    val status: String? = null,
    val artwork: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(name)
        parcel.writeString(artist)
        parcel.writeString(played_date)
        parcel.writeString(played_time)
        parcel.writeString(length_in_secs)
        parcel.writeString(status)
        parcel.writeString(artwork)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RadioMediaTrack> {
        override fun createFromParcel(parcel: Parcel): RadioMediaTrack {
            return RadioMediaTrack(parcel)
        }

        override fun newArray(size: Int): Array<RadioMediaTrack?> {
            return arrayOfNulls(size)
        }
    }
}