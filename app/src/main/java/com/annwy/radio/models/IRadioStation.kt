package com.annwy.radio.models

import android.os.Parcelable

interface IRadioStation : Parcelable {
    val radioName: String
    val radioUrl: String
    val regionName: String
    val logoUrl: String
}