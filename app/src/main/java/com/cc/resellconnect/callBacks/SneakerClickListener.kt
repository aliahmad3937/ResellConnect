package com.cc.resellconnect.callBacks

import com.cc.resellconnect.models.SneakerModel

interface SneakerClickListener {
    fun onSneakerClick(sneakerModel: SneakerModel)
}