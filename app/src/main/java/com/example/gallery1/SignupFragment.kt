package com.example.gallery1

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_signup.*
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE: Int=102
    private lateinit var imageUri: Uri
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mEmail:EditText
    private lateinit var mPassword:EditText
    private lateinit var mRegisterBtn:Button
    private lateinit var progreBar:ProgressBar
    private lateinit var mConfirmPassword:EditText
    private lateinit var mName:EditText
    private lateinit var userId:String
    private lateinit var profileLink:String

    private lateinit var auth: FirebaseAuth
    private lateinit var fstore:FirebaseFirestore




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
        var view:View= inflater.inflate(R.layout.fragment_signup, container, false)

         mRegisterBtn=view.findViewById(R.id.btn_register)
         mEmail=view.findViewById(R.id.EditText_email)
         mPassword=view.findViewById(R.id.EditText_pass)
         mName=view.findViewById(R.id.editText_name)
         mConfirmPassword=view.findViewById(R.id.EditText_confirmPass)
        progreBar=view.findViewById(R.id.progressBar)





        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        fstore= FirebaseFirestore.getInstance()


        if (auth.currentUser!=null){
            var intent=Intent(activity,Gallery1Activity::class.java)
            startActivity(intent)
        }



        mRegisterBtn.setOnClickListener {
            var email:String=mEmail.text.toString().trim()
            var password=mPassword.text.toString().trim()
            var confirmPass=mConfirmPassword.text.toString().trim()
            var name=mName.text.toString()

            if (TextUtils.isEmpty(email)){
                mEmail.setError("Email is Required")
                mEmail.requestFocus()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(password)){
                mPassword.setError("Password is requied")
                mPassword.requestFocus()
                return@setOnClickListener
            }
            if(password!=confirmPass){
                mConfirmPassword.setError("confirm Password should be same as Password")
                mConfirmPassword.requestFocus()
                return@setOnClickListener
            }
            progreBar.visibility=View.VISIBLE

            activity?.let { it1 ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(it1) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                           // val user = auth.currentUser


                            userId= auth.currentUser?.uid!!

                            var imageBitmap=imageView_progilesignup.drawable.toBitmap()

                           uplaodImageAndSaveUri(imageBitmap,email,password,name)
//
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                             Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
//
                        }

                        // ...
                    }
            }

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView_progilesignup.setOnClickListener {
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
            imageView_progilesignup.setImageBitmap(imageBitmap)


        }
    }

    private fun uplaodImageAndSaveUri(
        imageBitmap: Bitmap,
        email: String,
        password: String,
        name: String
    ){
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
                        var intent=Intent(activity,Gallery1Activity::class.java)
                        imageUri=it
                        imageView_progilesignup.setImageBitmap(imageBitmap)
                        Toast.makeText(activity,imageUri.toString(),Toast.LENGTH_SHORT).show()
                        image1=imageUri.toString()

                        val user = hashMapOf(
                            "email" to email,
                            "password" to password,
                            "name" to name,
                            "profileImage" to image1
                        )

// Add a new document with a generated ID
                        db.collection("users").document(userId).set(user as Map<String, Any>)
                            .addOnSuccessListener { documentReference ->
                                // Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                        startActivity(intent)

                    }
                }
            }else{
                uplaodTask.exception?.let {
                    Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)
    }

//    private fun updateUI(currentUser: FirebaseUser?) {
//
//    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
