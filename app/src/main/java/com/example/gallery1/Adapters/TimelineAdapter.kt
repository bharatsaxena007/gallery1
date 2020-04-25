package com.example.gallery1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery1.R
import com.example.gallery1.databinding.TimelineBinding
import com.example.gallery1.viewmodel.TimelineViewModel

class TimelineAdapter(
    private val context: Context,
    private val arrayList: ArrayList<TimelineViewModel>
) : RecyclerView.Adapter<TimelineAdapter.ItemView1>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView1 {
        val layoutInflater = LayoutInflater.from(parent.context)
        val timelineBinding: TimelineBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.timeline_list, parent, false)
        return ItemView1(timelineBinding)

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemView1, position: Int) {
        val timelineViewModel = arrayList[position]
        holder.bind(timelineViewModel)
    }

    class ItemView1(var timelineBinding: TimelineBinding) :
        RecyclerView.ViewHolder(timelineBinding.root) {
        fun bind(timelineViewModel: TimelineViewModel) {
            this.timelineBinding.timeline = timelineViewModel
            timelineBinding.executePendingBindings()
        }

    }

}


