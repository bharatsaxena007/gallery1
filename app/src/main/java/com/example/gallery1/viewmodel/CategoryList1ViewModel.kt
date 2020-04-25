package com.example.gallery1.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.gallery1.model.CategoryList1Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoryList1ViewModel:ViewModel {
    private lateinit var userId:String
    private lateinit var fAuth: FirebaseAuth

    var cat_image:String = ""
    var cat_title:String?=null
    var cat_id:String?=null
    constructor() : super()
    constructor(categoryList1Model:CategoryList1Model) : super() {
        this.cat_image = categoryList1Model.cat_image
        this.cat_title = categoryList1Model.cat_title
        this.cat_id = categoryList1Model.cat_id
    }


    fun getImageUrl():String{
        return cat_image
    }

    fun getArrayList(): MutableLiveData<ArrayList<CategoryList1ViewModel>> {
        var arraylistmutablelivedata= MutableLiveData<ArrayList<CategoryList1ViewModel>>()


        val db = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userId = fAuth.currentUser?.uid!!
        db.collection("users").document(userId).collection("category")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var arrayList=ArrayList<CategoryList1ViewModel>()
                    for (document in task.result!!) {
                        Log.d("test", document.id)
                        var id = document.id

                        var imageUrl = document.data.get("imageUrl").toString()
                        var title = document.data.getValue("title").toString()

                        var categoryList1Model=CategoryList1Model(imageUrl,title,id)
                        var categoryList1ViewModel: CategoryList1ViewModel =CategoryList1ViewModel(categoryList1Model)
                        arrayList!!.add(categoryList1ViewModel)
                        arraylistmutablelivedata.value=arrayList
                    }
                }
            }
        return arraylistmutablelivedata
    }

}
object CategoryBindingAdapter{
    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUrl(view: ImageView, url:String){
        Glide.with(view.context).load(url).into(view)
    }
}