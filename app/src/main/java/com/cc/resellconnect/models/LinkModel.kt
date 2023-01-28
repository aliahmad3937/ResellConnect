package com.cc.resellconnect.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LinkModel(
    val extraInfo:String?=null,
    val flipName:String?=null,
    val id:Long?=null,
    val image:String?=null,
    val isHidden:String?=null,
    var linkButtonTitle:String?=null,
    val linkURL:String?= null,
    val listingId:String?=null,
)

