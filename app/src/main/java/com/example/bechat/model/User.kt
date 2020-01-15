package com.example.bechat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
        var id: String? = null,
        var username: String?= null,
        var avatarURL: String?= null,
        var friends: MutableList<String>?= null
):Parcelable