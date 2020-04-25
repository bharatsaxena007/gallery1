package com.example.gallery1

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gallery1.MainActivity.Companion.manager
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mSignInBtn:Button



    private lateinit var auth: FirebaseAuth



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
        var view:View= inflater.inflate(R.layout.fragment_login, container, false)

        var btn_SignUp= view.findViewById<Button>(R.id.btn_SignUp)
        mSignInBtn=view.findViewById(R.id.btn_SignIn)
        mEmail=view.findViewById(R.id.EditText_emaillogin)
        mPassword=view.findViewById(R.id.EditText_passlogin)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser!=null){
            var intent=Intent(activity,Gallery1Activity::class.java)
            startActivity(intent)
        }

        btn_SignUp.setOnClickListener{
            manager.beginTransaction().replace(R.id.login_holder,
                SignupFragment.newInstance("1","2"))
                .addToBackStack(null).commit()

        }
        var btn_SignIn=view.findViewById<Button>(R.id.btn_SignIn)
        btn_SignIn.setOnClickListener {
            var email_login:String=mEmail.text.toString().trim()
            var password_login:String=mPassword.text.toString().trim()

            if (TextUtils.isEmpty(email_login)){
                mEmail.setError("Email is Required")
                mEmail.requestFocus()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(password_login)){
                mPassword.setError("Password is requied")
                mPassword.requestFocus()
                return@setOnClickListener
            }



            activity?.let { it1 ->
                auth.signInWithEmailAndPassword(email_login, password_login)
                    .addOnCompleteListener(it1) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "user logged in: successful")
                            Toast.makeText(context, "login successful", Toast.LENGTH_SHORT).show()
                            val user = auth.currentUser
                            var intent=Intent(activity,Gallery1Activity::class.java)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "loginUserWithEmail:failure", task.exception)
                            Toast.makeText(context, "login failed.", Toast.LENGTH_SHORT).show()

                        }
                    }
            }




        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
