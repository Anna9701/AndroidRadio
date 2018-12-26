package com.annwy.radio.models

import android.os.Parcel
import android.os.Parcelable

data class RadioStation(val radioName: String, val radioUrl: String, val regionName: String, val logoUrl: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun toString(): String = "$radioName - $regionName"
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(radioName)
        parcel.writeString(radioUrl)
        parcel.writeString(regionName)
        parcel.writeString(logoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RadioStation> {
        override fun createFromParcel(parcel: Parcel): RadioStation {
            return RadioStation(parcel)
        }

        override fun newArray(size: Int): Array<RadioStation?> {
            return arrayOfNulls(size)
        }
    }
}