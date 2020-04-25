package com.example.gallery1.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gallery1.Gallery1Activity

import com.example.gallery1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_image_display.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageDisplayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageDisplayFragment : Fragment() {
    private lateinit var userId:String
    private lateinit var fAuth: FirebaseAuth
    private var param1: String? = null
    private var param2: String? = null
    lateinit var  cid: String

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

        var root= inflater.inflate(R.layout.fragment_image_display, container, false)
        var bundle=arguments
        var id= bundle?.getString("data")!!
        cid=bundle?.getString("id")!!

        Log.d("display",id)

        fAuth = FirebaseAuth.getInstance()
        userId = fAuth.currentUser?.uid!!
        val db = FirebaseFirestore.getInstance()
        var documentReference = db.collection("users").document(userId)
            .collection("timeline").document(id)
        documentReference.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            var imageurl1= documentSnapshot?.getString("imageUrl")
            var cat_id=documentSnapshot?.getString("cat_id").toString()
            Log.d("cat_id",cat_id)
            Log.v("imageUrl",imageurl1+"url")
            if(imageurl1!=null)
            {
                Glide.with(this).load(imageurl1).into(imageView_display)
            }

            var btn_delete=root.findViewById(R.id.btn_delete) as FloatingActionButton
            btn_delete.setOnClickListener {
                db.collection("users").document(userId).collection("category")
                    .document(cat_id).collection("CategoryImages").document(id).delete()

                db.collection("users").document(userId).collection("timeline")
                    .document(id).delete()


               val categoryImagesFragment=CategoryImagesFragment()
                var b=Bundle()
                b.putString("data",cid)
                categoryImagesFragment.arguments=b
                Gallery1Activity.manager.beginTransaction().replace(R.id.home_frag,
                    categoryImagesFragment)
                    .addToBackStack(null).commit()

                Toast.makeText(context,"image deleted",Toast.LENGTH_SHORT).show()



            }

        }
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImageDisplayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
