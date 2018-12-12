package com.davis.kevin.dijkelapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun goToSchachten(view: View){
        val intent = Intent(this, SchachtenActivity::class.java)
        startActivity(intent)
    }
}
