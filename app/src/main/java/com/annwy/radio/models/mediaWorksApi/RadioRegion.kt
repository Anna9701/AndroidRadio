package com.annwy.radio.models.mediaWorksApi

import android.os.Parcel
import android.os.Parcelable
import com.beust.klaxon.Json

data class RadioRegion(
    @Json(name = "regionId")
    val regionName: String,
    val displayName: String,
    @Json(name = "relative_url")
    val relativeUrl: String,
    @Json(ignored = true)
    val radioUrl: String = "$RADIO_API_MEDIA_WORKS_URL$relativeUrl"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regionName)
        parcel.writeString(displayName)
        parcel.writeString(relativeUrl)
        parcel.writeString(radioUrl)
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