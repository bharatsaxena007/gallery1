package com.example.gallery1.Adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery1.Gallery1Activity
import com.example.gallery1.R
import com.example.gallery1.databinding.ImageBinding
import com.example.gallery1.view.CategoryImagesFragment
import com.example.gallery1.view.ImageDisplayFragment
import com.example.gallery1.viewmodel.ImageListViewModel


class ImageListAdapter(private val context: Context,private val arrayList: ArrayList<ImageListViewModel>,var id1:String)
    :RecyclerView.Adapter<ImageListAdapter.ItemView>() {

 val TAG="imagelist"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val layoutInflater=LayoutInflater.from(parent.context)
        val imageBinding:ImageBinding=DataBindingUtil.inflate(layoutInflater, R.layout.image_list,parent,false)
        return ItemView(imageBinding)
    }

    override fun getItemCount(): Int {
       return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemView, position: Int) {
       val imageListViewModel = arrayList[position]
        holder.bind(imageListViewModel)

        holder.imageBinding.imageList.setOnClickListener {
            var id=imageListViewModel.id
            Log.d(TAG,id)

            var bundle=Bundle()
            bundle.putString("data",id)
            bundle.putString("id",id1)
            val imageDisplayFragment=ImageDisplayFragment()
            imageDisplayFragment.arguments=bundle
            Gallery1Activity.manager.beginTransaction().replace(R.id.home_frag,
                imageDisplayFragment)
                .addToBackStack(null).commit()

        }

    }
    class ItemView(val imageBinding: ImageBinding):RecyclerView.ViewHolder(imageBinding.root){
        fun bind(imageListViewModel:ImageListViewModel){
            this.imageBinding.imagemodel=imageListViewModel
            imageBinding.executePendingBindings()


        }
    }
}