package com.example.gallery1.view

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery1.Adapters.ImageListAdapter

import com.example.gallery1.R
import com.example.gallery1.model.ImageListModel
import com.example.gallery1.viewmodel.ImageListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryImagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryImagesFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE: Int=103
    private lateinit var imageUri: Uri
    private var recyclerView:RecyclerView?=null
    private var imageListAdapter:ImageListAdapter?=null
    private var gridLayoutManager: GridLayoutManager? = null
    private lateinit var id:String
    private lateinit var btn_takepic:FloatingActionButton
    private lateinit var mtitle:String
    private lateinit var userId:String
    private lateinit var fAuth: FirebaseAuth
    private lateinit var imageUid:String

    val TAG="imageinfo"



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_category_images, container, false)
        fAuth = FirebaseAuth.getInstance()

        var bundle=arguments
        id= bundle?.getString("data")!!
        Log.d(TAG,id)
        userId = fAuth.currentUser?.uid!!
        val db = FirebaseFirestore.getInstance()


        var documentReference = db.collection("users").document(userId)
            .collection("category").document(id)
        documentReference.addSnapshotListener {
                documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot!!.exists()){
                mtitle= documentSnapshot?.getString("title")!!

            }


        }




        recyclerView=root.findViewById(R.id.recyler) as RecyclerView
        btn_takepic=root.findViewById(R.id.btn_takepic)
        var imageListViewModel:ImageListViewModel= ViewModelProviders.of(activity!!).get(ImageListViewModel::class.java)
        imageListViewModel.getdata(id)
        //var imageListViewModel:ImageListViewModel=ViewModelProvider(this)[ImageListViewModel::class.java]
        imageListViewModel.getArrayList(id).observe(activity!!, Observer {imageListViewModel->
            Log.v("exe","inside view model")
            Log.v("exe1",imageListViewModel.size.toString())
            imageListAdapter=context?.let { ImageListAdapter(it, imageListViewModel!!,id) }
            gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
            recyclerView?.layoutManager = gridLayoutManager
            recyclerView!!.adapter=imageListAdapter


        })


        return root
    }


    override fun onResume() {
        super.onResume()
        Log.v("exe","onResume")
        var imageListViewModel:ImageListViewModel= ViewModelProviders.of(activity!!).get(ImageListViewModel::class.java)
        imageListViewModel.getdata(id)
        //var imageListViewModel:ImageListViewModel=ViewModelProvider(this)[ImageListViewModel::class.java]
        imageListViewModel.getArrayList(id).observe(activity!!, Observer {imageListViewModel->
            Log.v("exe","inside view model")
            Log.v("exe12345",imageListViewModel.size.toString())
            imageListAdapter=context?.let { ImageListAdapter(it, imageListViewModel!!,id) }
            gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
            recyclerView?.layoutManager = gridLayoutManager
            recyclerView!!.adapter=imageListAdapter


        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_takepic.setOnClickListener {
            takepictureIntent()
        }
    }

    private fun takepictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent,REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode== Activity.RESULT_OK){
            val imageBitmap= data?.extras?.get("data") as Bitmap
            uplaodImageAndSaveUri1(imageBitmap)

        }
    }

    private fun uplaodImageAndSaveUri1(imageBitmap: Bitmap) {
        val baos= ByteArrayOutputStream()
        var imageId=Math.random().toString()
        val storafgeRef= FirebaseStorage.getInstance()
            .reference.child("categoryImage/${FirebaseAuth.getInstance().currentUser?.uid}/${mtitle}/${imageId}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val image=baos.toByteArray()
        val uplaod=storafgeRef.putBytes(image)
        uplaod.addOnCompleteListener{uplaodTask->
            if(uplaodTask.isSuccessful){
                storafgeRef.downloadUrl.addOnCompleteListener { urlTask->
                    urlTask.result?.let {
                        imageUri=it

                        Toast.makeText(activity,imageUri.toString(),Toast.LENGTH_SHORT).show()

                        uplaodImage(imageUri,imageId)




                    }
                }
            }else{
                uplaodTask.exception?.let {
                    Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun uplaodImage(imageUri: Uri, imageId: String) {

        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        val time = current.format(formatter)
        Log.d(TAG, time.toString())


        val user = hashMapOf(
            "imageUrl" to imageUri.toString(),
            "imageId" to  imageId,
            "timeStamp" to time,
            "cat_id"  to id
        )
        val db = FirebaseFirestore.getInstance()

// Add a new document with a generated ID
        db.collection("users").document(userId).collection("category").document(id)
            .collection("CategoryImages").add(user as Map<String, Any>)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                imageUid=documentReference.id
                Log.d(TAG,imageUid)
                db.collection("users").document(userId).collection("timeline").document(imageUid)
                    .set(user)
                Toast.makeText(activity,"image data saved",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }




    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryImagesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryImagesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
