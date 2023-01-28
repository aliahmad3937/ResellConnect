package com.cc.resellconnect.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cc.resellconnect.R
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.OnboardingSliderLayoutBinding
import com.cc.resellconnect.databinding.SliderLayoutBinding
import com.cc.resellconnect.models.SneakerModel

import com.smarteist.autoimageslider.SliderViewAdapter


class OnBoardingSliderAdapter(
    val mContext: Context,
    val mSliderItems: ArrayList<Int>,
    val listener: () -> Unit,
) : SliderViewAdapter<OnBoardingSliderAdapter.SliderAdapterViewHolder>() {

    class SliderAdapterViewHolder(val binding: OnboardingSliderLayoutBinding) : SliderViewAdapter.ViewHolder(binding.root)

    override fun getCount(): Int {
    return mSliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        return  SliderAdapterViewHolder(OnboardingSliderLayoutBinding
            .inflate(
            LayoutInflater.from(parent!!.context), parent, false
        ));
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder?, position: Int) {
       if(position == 5){
           viewHolder?.binding?.lastScreen!!.visibility = View.VISIBLE
           viewHolder?.binding?.myimage!!.visibility = View.GONE
       }else{
           viewHolder?.binding?.lastScreen!!.visibility = View.GONE
           viewHolder?.binding?.myimage!!.visibility = View.VISIBLE
       }

        viewHolder.binding.myimage.setImageDrawable(mContext.getDrawable(mSliderItems.get(position)))


        viewHolder.binding.btnContinue.setOnClickListener {
            listener()
        }

    }

}