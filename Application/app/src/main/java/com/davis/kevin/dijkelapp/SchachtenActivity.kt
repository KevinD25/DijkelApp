package com.davis.kevin.dijkelapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.FirebaseDatabase

class SchachtenActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var customView : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schachten)
        database = FirebaseDatabase.getInstance()
    }

    fun openDialog(view: View) {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.customschachtdialog)

        }

        // Setup custom view content
         customView = dialog.getCustomView() ?: return
        val voornaamInput: EditText = customView.findViewById(R.id.txtVoornaam)
        val achternaamInput: EditText = customView.findViewById(R.id.txtAchternaam)
        val buttonConfirm: Button = customView.findViewById(R.id.btnConfirm)
        val buttonCancel: Button = customView.findViewById(R.id.btnCancel)

        buttonConfirm.setOnClickListener {
            onClickConfirm(voornaamInput, achternaamInput)
        }
        buttonCancel.setOnClickListener {
            onClickCancel()
        }

    }

    fun onClickConfirm(voornaamInput: EditText, achternaamInput: EditText) {

        var vn: String = voornaamInput.text.toString().trim()
        var an: String = achternaamInput.text.toString().trim()
        if (checkFields(vn, an)) {

            val myRef = database.getReference("schachten")
            val schachtId = myRef.push().key.toString()
            val schacht = Schacht(schachtId, vn, an, 0)
            myRef.child(schachtId).setValue(schacht).addOnCompleteListener{
                //TODO CLOSE DIALOG
            }


        } else {
            Toast.makeText(this, "Vul alle velden in...", Toast.LENGTH_SHORT).show()
        }

    }

    fun checkFields(voornaam: String, achternaam: String): Boolean {
        //return !(voornaam.isNullOrEmpty() || achternaam.isNullOrEmpty() || voornaam.isNullOrBlank() || achternaam.isNullOrBlank())
        return voornaam != "" || achternaam != ""
    }

    fun onClickCancel() {
        //TODO CLOSE DIALOG
    }


}
