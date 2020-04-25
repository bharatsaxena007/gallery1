package com.example.gallery1.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery1.Adapters.CategoryList1Adapter
import com.example.gallery1.view.AddCategoryFragment

import com.example.gallery1.R
import com.example.gallery1.viewmodel.CategoryList1ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Category1.newInstance] factory method to
 * create an instance of this fragment.
 */
class Category1 : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var categoryList1Adapter: CategoryList1Adapter
    private var gridLayoutManager: GridLayoutManager? = null
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
        var root=inflater.inflate(R.layout.fragment_category1, container, false)
       var recyclerView = root.findViewById(R.id.category_recyclerview) as RecyclerView
        var categoryList1ViewModel:CategoryList1ViewModel=ViewModelProvider(this)[CategoryList1ViewModel::class.java]
        categoryList1ViewModel.getArrayList().observe(this.viewLifecycleOwner, Observer { categoryList1ViewModel->

            categoryList1Adapter= context?.let { CategoryList1Adapter(it,categoryList1ViewModel) }!!
            gridLayoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
            recyclerView?.layoutManager = gridLayoutManager
            recyclerView!!.adapter=categoryList1Adapter

        })
        var btnAddCategory = root.findViewById<FloatingActionButton>(R.id.btnAddCategory)
        btnAddCategory.setOnClickListener {
            Toast.makeText(activity, "add btn clicked", Toast.LENGTH_SHORT).show()
            val addCategoryFragment = AddCategoryFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.home_frag, addCategoryFragment)
                ?.addToBackStack(null)?.commit()

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
         * @return A new instance of fragment Category1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Category1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
