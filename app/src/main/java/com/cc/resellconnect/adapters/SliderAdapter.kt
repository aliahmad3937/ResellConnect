package com.cc.resellconnect.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cc.resellconnect.R
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.SliderLayoutBinding
import com.cc.resellconnect.models.SneakerModel
import com.cc.resellconnect.utils.MyApplication

import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter(
    val mContext: Context,
    val mSliderItems: ArrayList<String>,
    val model: SneakerModel,
    val sneakerClickListener: SneakerClickListener
) : SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {

    class SliderAdapterViewHolder(val binding: SliderLayoutBinding) : SliderViewAdapter.ViewHolder(binding.root)

    override fun getCount(): Int {
    return mSliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        return  SliderAdapterViewHolder(SliderLayoutBinding
            .inflate(
            LayoutInflater.from(parent!!.context), parent, false
        ));
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder?, position: Int) {
        if(MyApplication.isGuest || MyApplication.isFree){
            viewHolder?.binding!!.myimage.setImageDrawable(mContext.getDrawable(R.drawable.lock))
        }else {
            Glide.with(viewHolder!!.itemView)
                .load(mSliderItems.get(position))
                .into(viewHolder.binding.myimage);
        }

        viewHolder?.itemView!!.setOnClickListener {
            sneakerClickListener.onSneakerClick(model)
        }
    }

}