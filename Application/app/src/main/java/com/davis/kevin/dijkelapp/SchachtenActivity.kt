package com.davis.kevin.dijkelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView

class SchachtenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schachten)
    }

    fun openDialog(view:View){
        MaterialDialog(this).show {
            customView(R.layout.customschachtdialog)
        }
    }
}
