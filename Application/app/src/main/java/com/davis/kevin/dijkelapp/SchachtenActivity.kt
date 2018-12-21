package com.davis.kevin.dijkelapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.Adapters.SchachtenLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_schachten.*

class SchachtenActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var customView: View
    private lateinit var reference: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schachten)
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")


       // val emptylist = Schacht("1337", "Nog geen", "schachten toegevoegd...", 0)
        //schachtenLijst.add(emptylist)

        fireBaseGet()


    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
               /* if (schachtenLijst[0].id == "1337") {
                    schachtenLijst.clear()
                }*/
                if (dataSnapshot.exists()) {
                    for (h in dataSnapshot.children) {
                        schacht = h.getValue(Schacht::class.java)
                        schachtenLijst.add(schacht!!)
                    }
                    val adapter = SchachtenLijstAdapter(applicationContext, schachtenLijst)
                    listSchachtenAdmin.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(schachtListener)
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
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun onClickConfirm(voornaamInput: EditText, achternaamInput: EditText) {

        var vn: String = voornaamInput.text.toString().trim()
        var an: String = achternaamInput.text.toString().trim()
        if (checkFields(vn, an)) {

            val myRef = database.getReference("schachten")
            val schachtId = myRef.push().key.toString()
            val schacht = Schacht(schachtId, vn, an, 0)
            myRef.child(schachtId).setValue(schacht).addOnCompleteListener {
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
}
