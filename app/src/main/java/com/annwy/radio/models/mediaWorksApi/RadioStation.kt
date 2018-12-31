package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable
import com.annwy.radio.models.IRadioStation
import com.google.gson.annotations.SerializedName

data class RadioStation(
    @SerializedName("name")
    override val radioName: String,
    val shows: ArrayList<RadioShow>,
    val nowPlaying: ArrayList<RadioMediaTrack>,
    val previouslyPlayed: ArrayList<RadioMediaTrack>,
    val contactInformation: RadioContactInformation,
    val audioRenditions: ArrayList<RadioAudioRendition>,
    @SerializedName("region")
    override val regionName: String,
    val regionList: ArrayList<RadioRegion>? = null
) : IRadioStation {
    override val radioUrl: String
        get() = audioRenditions.first().url ?: String()
    override val logoUrl: String
        get() = shows.first().logo

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(RadioShow.CREATOR),
        parcel.createTypedArrayList(RadioMediaTrack.CREATOR),
        parcel.createTypedArrayList(RadioMediaTrack.CREATOR),
        parcel.readParcelable(RadioContactInformation::class.java.classLoader),
        parcel.createTypedArrayList(RadioAudioRendition.CREATOR),
        parcel.readString(),
        parcel.createTypedArrayList(RadioRegion.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(radioName)
        parcel.writeTypedList(shows)
        parcel.writeTypedList(nowPlaying)
        parcel.writeTypedList(previouslyPlayed)
        parcel.writeParcelable(contactInformation, flags)
        parcel.writeTypedList(audioRenditions)
        parcel.writeString(regionName)
        parcel.writeTypedList(regionList)
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