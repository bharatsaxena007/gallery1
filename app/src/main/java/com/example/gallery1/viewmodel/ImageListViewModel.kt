package com.example.gallery1.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.gallery1.model.ImageListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class ImageListViewModel:ViewModel, LifecycleObserver {


    private var id1: String? = null
    val TAG="viewmodel"
    private lateinit var userId:String
    private lateinit var fAuth: FirebaseAuth


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

    fun getdata(id:String) {
        val db = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userId = fAuth.currentUser?.uid!!
        var arraylistmutablelivedata = MutableLiveData<ArrayList<ImageListViewModel>>()
        var ref = db.collection("users").document(userId).collection("category").document(id.toString())
            .collection("CategoryImages")
          ref.addSnapshotListener{
              snap,e->
              var a= snap!!.documents
              Log.v("snap--",a.toString())

          }

    }

    fun getArrayList(id1:String): MutableLiveData<ArrayList<ImageListViewModel>> {



        val db = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userId = fAuth.currentUser?.uid!!
        Log.d(TAG,id1)
        var arraylistmutablelivedata=MutableLiveData<ArrayList<ImageListViewModel>>()
        val source = Source.CACHE

        db.collection("users").document(userId).collection("category").document(id1)
            .collection("CategoryImages")
            .get(source)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    var arrayList=ArrayList<ImageListViewModel>()

                    var a=task.result!!.size()
                    Log.v("a===",a.toString())
                    for(document in task.result!!){
                        Log.d(TAG,document.id)
                        var id=document.id
                        var imageUrl=document.data.get("imageUrl").toString()
                        Log.v("data--",id)
                        var timeStamp=document.data.get("timeStamp").toString()
                        var imageListModel1=ImageListModel(id,imageUrl,timeStamp)
                        var imageListViewModel1:ImageListViewModel= ImageListViewModel(imageListModel1)
                        arrayList!!.add(imageListViewModel1)
                        arraylistmutablelivedata.value=arrayList



                    }

                }
            }


        return arraylistmutablelivedata

    }

}






object ImageBindingAdapter{
    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUrl(view:ImageView,url:String){
        Glide.with(view.context).load(url).into(view)
    }
}