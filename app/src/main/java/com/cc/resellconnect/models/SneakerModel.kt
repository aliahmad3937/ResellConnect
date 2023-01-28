package com.cc.resellconnect.models

import android.os.Parcel
import android.os.Parcelable

data class SneakerModel(
    val date:String?=null,
    val expired:String?=null,
    val highBid:String?=null,
    val id:Long?=null,
    val image:String?=null,
    val image2:String?=null,
    val image3:String?=null,
    val info:String?=null,
    val infoSecondary:String?=null,
    val isHidden:String?=null,
    var likes:Int?=null,
    val lowBid:String?= null,
    val name:String?=null,
    val viewOfferInfo:String?=null,
    val viewOfferTitle:String?=null,
    val viewOfferURL:String?=null,
    var flipId:String?=null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }



    companion object CREATOR : Parcelable.Creator<SneakerModel> {
        override fun createFromParcel(parcel: Parcel): SneakerModel {
            return SneakerModel(parcel)
        }

        override fun newArray(size: Int): Array<SneakerModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
     //  TODO("Not yet implemented")
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
      //  TODO("Not yet implemented")
    }
}
