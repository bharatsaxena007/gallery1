package com.example.gallery1.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.gallery1.model.ImageListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimelineViewModel:ViewModel {
    var id:String =""
    var imageUrl1:String = ""
    var timeStamp=""

    constructor() : super()
    constructor(imageListModel: ImageListModel) : super() {
        this.id = imageListModel.id
        this.imageUrl1 = imageListModel.imageUrl1
        this.timeStamp = imageListModel.timeStamp
    }


    fun getImageUrl():String{
        return imageUrl1
    }


    fun getArrayList(): MutableLiveData<ArrayList<TimelineViewModel>> {
        val db = FirebaseFirestore.getInstance()
       var fAuth = FirebaseAuth.getInstance()
       var userId = fAuth.currentUser?.uid!!
        var arraylistmutablelivedata= MutableLiveData<ArrayList<TimelineViewModel>>()

        db.collection("users").document(userId).collection("timeline")
            .get()
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    var arrayList=ArrayList<TimelineViewModel>()
                    for(document in task.result!!){
                        var imageUrl=document.data.get("imageUrl").toString()
                        var timestamp1:String= document.data.get("timeStamp").toString()

                        var imageListModel1=ImageListModel("1",imageUrl,timestamp1)
                        var timelineViewModel:TimelineViewModel= TimelineViewModel(imageListModel1)
                        arrayList!!.add(timelineViewModel)
                        arrayList.sortByDescending({selector(it)})
                        arraylistmutablelivedata.value=arrayList
                    }

                }
            }

        return arraylistmutablelivedata

    }
    fun selector(p: TimelineViewModel): String = p.timeStamp
}
object TimelineBindingAdapter{
    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUrl(view: ImageView, url:String){
        Glide.with(view.context).load(url).into(view)
    }
}