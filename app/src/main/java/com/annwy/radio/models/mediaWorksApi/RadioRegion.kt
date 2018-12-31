package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RadioRegion(
    @SerializedName("regionId")
    val regionName: String,
    val displayName: String,
    @SerializedName("relative_url")
    val relativeUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    val radioUrl: String
        get() = "$RADIO_API_MEDIA_WORKS_URL$relativeUrl"


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regionName)
        parcel.writeString(displayName)
        parcel.writeString(relativeUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RadioRegion> {
        const val RADIO_API_MEDIA_WORKS_URL = "https://radio-api.mediaworks.nz"

        override fun createFromParcel(parcel: Parcel): RadioRegion {
            return RadioRegion(parcel)
        }

        override fun newArray(size: Int): Array<RadioRegion?> {
            return arrayOfNulls(size)
        }
    }
}