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
import android.widget.EditText
import android.widget.Toast
import com.example.gallery1.R
import com.example.gallery1.view.Category1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_category.*
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCategoryFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE=101
    private lateinit var imageUri:Uri
    private lateinit var mTitle:EditText


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

     var auth = FirebaseAuth.getInstance()
    var userId=auth.currentUser?.uid.toString()
    val db = FirebaseFirestore.getInstance()
    var fstore= FirebaseFirestore.getInstance()

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
        val view= inflater.inflate(R.layout.fragment_add_category, container, false)
        mTitle=view.findViewById(R.id.category_title)
        var title=mTitle.text.toString()

        return view
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         imageView_category.setOnClickListener {
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
            uplaodImageAndSaveUri(imageBitmap)

        }
    }

    private fun uplaodImageAndSaveUri(imageBitmap: Bitmap) {

        val baos=ByteArrayOutputStream()
        var title=mTitle.text.toString()

        val storafgeRef=FirebaseStorage.getInstance()
            .reference.child("categoryImage/${FirebaseAuth.getInstance().currentUser?.uid}/${title}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val image=baos.toByteArray()
        val uplaod=storafgeRef.putBytes(image)
        uplaod.addOnCompleteListener{uplaodTask->
            if(uplaodTask.isSuccessful){
                storafgeRef.downloadUrl.addOnCompleteListener { urlTask->
                    urlTask.result?.let {
                        imageUri=it
                        imageView_category.setImageBitmap(imageBitmap)
                        Toast.makeText(activity,imageUri.toString(),Toast.LENGTH_SHORT).show()
                        var title=mTitle.text.toString()
                        uplaodImageAndTitle(imageUri,title)




                    }
                }
            }else{
                uplaodTask.exception?.let {
                    Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun uplaodImageAndTitle(imageUri: Uri, title: String) {
        btn_save_category.setOnClickListener {

            val user = hashMapOf(
                "imageUrl" to imageUri.toString(),
                "title" to  title
            )

// Add a new document with a generated ID
            db.collection("users").document(userId).collection("category").add(user as Map<String, Any>)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
            Toast.makeText(activity,"category data saved",Toast.LENGTH_SHORT).show()
            val category= Category1()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.home_frag,category)
                ?.addToBackStack(null)?.commit()
        }



        }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
