package com.example.gallery1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var manager: FragmentManager
        var instance: MainActivity? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainActivity.manager = this.supportFragmentManager
        val transaction = MainActivity.manager.beginTransaction()
        val login_fragment = LoginFragment()
        transaction.add(R.id.login_holder, login_fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
