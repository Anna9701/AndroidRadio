package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable

data class RadioContactInformation(
    val textKeyword: String,
    val facebookUrl: String,
    val phoneNumber: String,
    val text: String,
    val websiteUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(textKeyword)
        parcel.writeString(facebookUrl)
        parcel.writeString(phoneNumber)
        parcel.writeString(text)
        parcel.writeString(websiteUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RadioContactInformation> {
        override fun createFromParcel(parcel: Parcel): RadioContactInformation {
            return RadioContactInformation(parcel)
        }

        override fun newArray(size: Int): Array<RadioContactInformation?> {
            return arrayOfNulls(size)
        }
    }
}