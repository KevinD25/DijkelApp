package com.davis.kevin.dijkelapp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.Adapters.SchachtenLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_schachten.*

class SchachtenActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var customView: View
    private lateinit var reference: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()
    lateinit var item: Schacht
    private lateinit var dijkelref: DatabaseReference
    private var dijkel: Dijkel? = null
    private var dijkelLijst: MutableList<Dijkel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schachten)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")

        fireBaseGet()

        listSchachtenAdmin.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // This is your listview's selected item
            item = parent.getItemAtPosition(position) as Schacht
            openEditActivity()
        }
    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                /* if (schachtenLijst[0].id == "1337") {
                     schachtenLijst.clear()
                 }*/
                if (dataSnapshot.exists()) {
                    schachtenLijst.clear()
                    for (h in dataSnapshot.children) {
                        schacht = h.getValue(Schacht::class.java)
                        schachtenLijst.add(schacht!!)
                    }
                    schachtenLijst.sortBy { it.voornaam }
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

        val dijkelListener = object : ValueEventListener {
            override fun onDataChange(dijkelSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dijkelSnapshot.exists()) {
                    dijkelLijst.clear()

                    for (h in dijkelSnapshot.children) {
                        dijkel = h.getValue(Dijkel::class.java)
                        dijkelLijst.add(dijkel!!)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        dijkelref.addValueEventListener(dijkelListener)
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

    fun openDeleteDialog(view: View) {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.deletealldialog)
        }

        // Setup custom view content
        customView = dialog.getCustomView() ?: return
        val buttonDeleteAll: Button = customView.findViewById(R.id.btnDeleteAll)
        val buttonCancel: Button = customView.findViewById(R.id.btnCancel)

        buttonDeleteAll.setOnClickListener {
            onClickDeleteAll()
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun openEditActivity() {
        val intent = Intent(this, SchachtDetailActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    fun onClickDeleteAll() {
        var schachtlijst : MutableList<String> = mutableListOf()
        var dijkellijst : MutableList<String> = mutableListOf()
        for (item in schachtenLijst) {
            schachtlijst.add(item.id)

        }
        for(item in schachtlijst){
            reference.child(item).setValue(null)
        }
        for (dijkel in dijkelLijst) {
            dijkellijst.add(dijkel.id)

        }
        for(dijkel in dijkellijst){
            dijkelref.child(dijkel).setValue(null)
        }

        finish();
        startActivity(getIntent());
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
        return voornaam != "" && achternaam != ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

