package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable

data class RadioShow(
    val name: String,
    val startTime: String,
    val startUTC: Long,
    val endTime: String,
    val endUTC: Long,
    val hero: String,
    val logo: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(startTime)
        parcel.writeLong(startUTC)
        parcel.writeString(endTime)
        parcel.writeLong(endUTC)
        parcel.writeString(hero)
        parcel.writeString(logo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RadioShow> {
        override fun createFromParcel(parcel: Parcel): RadioShow {
            return RadioShow(parcel)
        }

        override fun newArray(size: Int): Array<RadioShow?> {
            return arrayOfNulls(size)
        }
    }
}