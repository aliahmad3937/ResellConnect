package com.cc.resellconnect.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cc.resellconnect.R
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.ItemBottomLinkBinding
import com.cc.resellconnect.databinding.ItemSneakerBinding
import com.cc.resellconnect.models.LinkModel
import com.cc.resellconnect.models.SneakerModel
import com.cc.resellconnect.utils.SavedPreference
import com.google.firebase.firestore.FirebaseFirestore
import com.smarteist.autoimageslider.SliderView
import java.util.*


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class LinksAdapter(
    private var mList: List<LinkModel>,
    val context: Context,
) : RecyclerView.Adapter<LinksViewHolder>() {

    val visitLinkList: MutableList<Long> = SavedPreference.getVisitLinkList(context) as MutableList<Long>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksViewHolder {
        return LinksViewHolder(
            ItemBottomLinkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LinksViewHolder, position: Int) {
        val model = mList[position]
        if (visitLinkList.contains(model.id)) {
            holder.mBinding.linkVisit.visibility = View.VISIBLE
            holder.mBinding.linkBtn.visibility = View.INVISIBLE
        } else {
            holder.mBinding.linkVisit.visibility = View.INVISIBLE
            holder.mBinding.linkBtn.visibility = View.VISIBLE
        }

        holder.mBinding.linkText.text = model.extraInfo

        Glide.with(context).load(model.image).placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder).into(holder.mBinding.linkImg)


        holder.itemView.setOnClickListener {
            if (!visitLinkList.contains(model.id)) {
                visitLinkList.add(model.id!!)
                SavedPreference.saveVisiLinkList(context, visitLinkList)
            }

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.linkURL))
            context.startActivity(browserIntent)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}

class LinksViewHolder(val mBinding: ItemBottomLinkBinding) : RecyclerView.ViewHolder(mBinding.root)