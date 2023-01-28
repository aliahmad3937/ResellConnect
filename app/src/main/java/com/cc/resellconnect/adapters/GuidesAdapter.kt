package com.cc.resellconnect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cc.resellconnect.callBacks.GuideClickListener
import com.cc.resellconnect.databinding.ItemGuideGridBinding
import com.cc.resellconnect.models.GuideModel
import com.cc.resellconnect.utils.MyApplication
import java.util.*


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class GuidesAdapter(
    private var mList: List<GuideModel>,
    val context: Context,
    val guideClickListener: GuideClickListener,

    ): RecyclerView.Adapter<ChatHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        return ChatHomeViewHolder(
            ItemGuideGridBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder,  position: Int) {

        if(MyApplication.isFree || MyApplication.isGuest) {
            if (mList[position].isFree == "true") {
                // holder.mBinding.layoutPdf.visibility = View.VISIBLE
                holder.mBinding.lock.visibility = View.INVISIBLE

            } else {
                //  holder.mBinding.layoutPdf.visibility = View.INVISIBLE
                holder.mBinding.lock.visibility = View.VISIBLE
            }
        }

        holder.mBinding.tvTitle.text = mList[position].name
        holder.mBinding.tvTime.text = "Last updated: "+mList[position].date


//        holder.mBinding.tvTitle.text = mList[position].name+mList.get(position).isHidden
//        holder.mBinding.tvTime.text = "Last updated: "+mList[position].date+mList.get(position).isFree




        holder.itemView.setOnClickListener {
            if (MyApplication.isFree || MyApplication.isGuest){
                if(mList[position].isFree == "true") {
                    guideClickListener.onGuideClick(mList[position])
                }
            }else{
                guideClickListener.onGuideClick(mList[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }



}
class ChatHomeViewHolder(val mBinding: ItemGuideGridBinding) : RecyclerView.ViewHolder(mBinding.root)