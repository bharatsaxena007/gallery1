package com.example.gallery1.view.gallery

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.gallery1.MainActivity
import com.example.gallery1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.ByteArrayOutputStream

class GalleryFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 100
    private val TAG: String = "profile"
    private lateinit var imageUri: Uri

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var btn_logout: Button
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var name: TextView
    private lateinit var btn_changePic: Button

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseStorage

    private lateinit var userId: String
    private lateinit var mUser: User
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mEmail:String
    private lateinit var mPassword:String
    private lateinit var mName:String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        var profile_image = root.findViewById<ImageView>(R.id.imageView_profile)
        email = root.findViewById(R.id.Text_email)
        password = root.findViewById(R.id.Text_pass)
        name = root.findViewById(R.id.Text_name)
        btn_changePic = root.findViewById(R.id.btn_changeprofilepic)


        var user = FirebaseAuth.getInstance().currentUser
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseStorage.getInstance()


        btn_logout = root.findViewById(R.id.btn_logout)
        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
            var intent = Intent(activity, MainActivity::class.java)
            activity?.finish()
            startActivity(intent)
        }


        userId = fAuth.currentUser?.uid!!
        val db = FirebaseFirestore.getInstance()


        var documentReference = db.collection("users").document(userId)
        documentReference.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
           mEmail= documentSnapshot?.getString("email")!!
            mPassword= documentSnapshot?.getString("password")!!
            mName= documentSnapshot?.getString("name")!!
            email.setText(mEmail)
            password.setText(mPassword)
            name.setText(mName)


            var profile_image1 = documentSnapshot?.getString("profileImage")
            Toast.makeText(activity, profile_image1, Toast.LENGTH_SHORT).show()

            Glide.with(this).load(profile_image1).into(profile_image)

        }
//        imageView_profile.setOnClickListener {
//            takepictureIntent()
//        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btn_changePic.setOnClickListener {
//            takepictureIntent()
//        }

    }

    private fun takepictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView_profile.setImageBitmap(imageBitmap)
            uplaodImageAndSaveUri(imageBitmap,mEmail,mPassword,mName)

        }
    }

    private fun uplaodImageAndSaveUri(imageBitmap: Bitmap, mEmail: String, mPassword: String, mName: String) {

        var image1:String=""

        val baos= ByteArrayOutputStream()
        var db=FirebaseFirestore.getInstance()
        val storafgeRef= FirebaseStorage.getInstance()
            .reference.child("profileImages/${FirebaseAuth.getInstance().currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val image=baos.toByteArray()
        val uplaod=storafgeRef.putBytes(image)

        uplaod.addOnCompleteListener{uplaodTask->
            if(uplaodTask.isSuccessful){
                storafgeRef.downloadUrl.addOnCompleteListener { urlTask->
                    urlTask.result?.let {

                        imageUri=it
                        imageView_profile.setImageBitmap(imageBitmap)
                        Toast.makeText(activity,imageUri.toString(),Toast.LENGTH_SHORT).show()
                        image1=imageUri.toString()

                        val user = hashMapOf(
                            "email" to mEmail,
                            "password" to mPassword,
                            "name" to mName,
                            "profileImage" to image1
                        )

// Add a new document with a generated ID
                        db.collection("users").document(userId).set(user as Map<String, Any>)
                            .addOnSuccessListener { documentReference ->
                                // Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error adding document", e)
                            }

                    }
                }
            }else{
                uplaodTask.exception?.let {
                    Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                }
            }

        }

    }


}
