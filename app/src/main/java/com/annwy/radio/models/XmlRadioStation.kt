package com.annwy.radio.models

import android.os.Parcel
import android.os.Parcelable

data class XmlRadioStation(override val radioName: String, override val radioUrl: String, override val regionName: String, override val logoUrl: String) : IRadioStation {
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

    companion object CREATOR : Parcelable.Creator<XmlRadioStation> {
        override fun createFromParcel(parcel: Parcel): XmlRadioStation {
            return XmlRadioStation(parcel)
        }

        override fun newArray(size: Int): Array<XmlRadioStation?> {
            return arrayOfNulls(size)
        }
    }
}