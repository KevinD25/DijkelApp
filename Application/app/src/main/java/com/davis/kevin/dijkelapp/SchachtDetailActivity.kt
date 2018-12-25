package com.davis.kevin.dijkelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.davis.kevin.dijkelapp.Adapters.DijkeltjesAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dijkel.*
import kotlinx.android.synthetic.main.activity_schacht_detail.*

class SchachtDetailActivity : AppCompatActivity() {

    lateinit var item: Schacht
    private var id: String = ""
    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var dijkelref: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()
    private var dijkel: Dijkel? = null
    private var dijkelLijst: MutableList<Dijkel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schacht_detail)
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")
        id = intent.getStringExtra("id")
        fireBaseGet()
    }

    fun fillInfo() {
        txtEditSchachtNaam.text = item.voornaam + " " + item.achternaam
        editVoornaam.setText(item.voornaam)
        editAchternaam.setText(item.achternaam)
    }

    fun getClickedSchacht() {
        for (schacht in schachtenLijst) {
            if (schacht.id == id) {
                item = schacht
            }
        }
        fillInfo()
    }

    fun onClickEdit(view:View){
        val voornaam = editVoornaam.text.toString().trim()
        val achternaam = editAchternaam.text.toString().trim()

        if(voornaam.isEmpty())editVoornaam.error = "Empty field"
        if(achternaam.isEmpty()) editAchternaam.error ="Empty field"

        if(voornaam.isNotEmpty() && achternaam.isNotEmpty()){
            val schacht = Schacht(item.id, voornaam, achternaam, item.dijkels)
            reference.child(schacht.id).setValue(schacht)
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    fun onClickDelete(view:View){
        reference.child(item.id).setValue(null)
        for(dijkel in dijkelLijst) {
            if(dijkelref.child(dijkel.id).child("schachtid").toString() == item.id){
                dijkelref.child(dijkel.id).setValue(null)
            }
        }
        super.onBackPressed()
    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    schachtenLijst.clear()
                    for (h in dataSnapshot.children) {
                        schacht = h.getValue(Schacht::class.java)
                        schachtenLijst.add(schacht!!)
                    }
                    getClickedSchacht()
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
}
