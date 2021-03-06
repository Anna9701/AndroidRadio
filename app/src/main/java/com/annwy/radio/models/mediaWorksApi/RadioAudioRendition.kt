package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RadioAudioRendition(
    @SerializedName("brightcoveId")
    val id: String? = null,
    val url: String? = null,
    val quality: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(url)
        parcel.writeString(quality)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RadioAudioRendition> {
        override fun createFromParcel(parcel: Parcel): RadioAudioRendition {
            return RadioAudioRendition(parcel)
        }

        override fun newArray(size: Int): Array<RadioAudioRendition?> {
            return arrayOfNulls(size)
        }
    }
}