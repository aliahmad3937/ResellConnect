package com.cc.resellconnect.callBacks

import com.cc.resellconnect.models.GuideModel

interface GuideClickListener {
    fun onGuideClick(guideModel: GuideModel)
}