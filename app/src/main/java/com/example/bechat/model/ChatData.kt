package com.example.bechat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ChatData (
        var mess: String?= null,
        var time: String?= null,
        var idSender: String?= null,
        var idReceiver : String?= null,
        var type: String?= null
):Parcelable