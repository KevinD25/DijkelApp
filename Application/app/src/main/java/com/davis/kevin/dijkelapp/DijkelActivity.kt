package com.davis.kevin.dijkelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.Adapters.DijkelLijstAdapter
import com.davis.kevin.dijkelapp.Adapters.DijkeltjesAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dijkel.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dijkeldetailcustom.*
import java.text.SimpleDateFormat
import java.util.*

class DijkelActivity : AppCompatActivity() {

    lateinit var item: Schacht
    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var dijkelref: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()
    private var dijkel: Dijkel? = null
    private var tempDijkelLijst: MutableList<Dijkel> = mutableListOf()
    private var dijkelLijst: MutableList<Dijkel> = mutableListOf()
    private var id: String = ""
    lateinit var customView: View
    lateinit var checkbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dijkel)
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")

        id = intent.getStringExtra("id")
        fireBaseGet()

        setAdapter()

    }

    fun getClickedSchacht() {
        for (schacht in schachtenLijst) {
            if (schacht.id == id) {
                item = schacht
            }
        }

        txtNaam.text = item.voornaam + " " + item.achternaam
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
                    tempDijkelLijst.clear()
                    for (h in dijkelSnapshot.children) {
                        dijkel = h.getValue(Dijkel::class.java)
                        tempDijkelLijst.add(dijkel!!)
                    }

                    for (item in tempDijkelLijst) {
                        if (item.schachtid == id) {
                            dijkelLijst.add(item)
                        }
                    }


                    setAdapter()


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

    private fun setAdapter() {
        dijkelLijst.sortByDescending { it.datum }
        dijkelLijst.sortBy { it.done }
        val sortedDijkelLijst: List<Dijkel> = dijkelLijst.sortedWith(compareBy({ it.done }))
        val adapter = DijkeltjesAdapter(applicationContext, dijkelLijst)
        listDijkels.adapter = adapter
    }

    fun addDijkel(view: View) {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.customdijkeldialog)
        }

        customView = dialog.getCustomView() ?: return


        val aantalDijkels: EditText = customView.findViewById(R.id.txtAantalDijkels)
        val redenDijkel: EditText = customView.findViewById(R.id.txtReden)
        val buttonConfirm: Button = customView.findViewById(R.id.btnConfirm)
        val buttonCancel: Button = customView.findViewById(R.id.btnCancel)

        buttonConfirm.setOnClickListener {
            onClickConfirm(aantalDijkels, redenDijkel, dialog)
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun onClickConfirm(aantalDijkels: EditText, redenDijkel: EditText, dialog: MaterialDialog) {

        if (aantalDijkels.text.toString().trim() != "") {
            if (checkFields(aantalDijkels, redenDijkel)) {

                val myRef = database.getReference("dijkels")
                var aantal: Int = aantalDijkels.text.toString().trim().toInt()
                var reden: String = redenDijkel.text.toString().trim()
                val donor: String = currentUser.username
                for (i in 1..aantal) {
                    val dijkelId = myRef.push().key.toString()
                    val sdf = SimpleDateFormat("dd/M/yyyy")
                    val currentDate = sdf.format(Date())
                    val dijkel =
                        Dijkel(dijkelId, item.id, reden, donor, false, currentDate)
                    myRef.child(dijkelId).setValue(dijkel).addOnCompleteListener {}
                }
                dialog.dismiss()

            } else {
                if (aantalDijkels.text.toString().trim() == "") aantalDijkels.setError("Empty field!")
                if (redenDijkel.text.toString().trim() == "") redenDijkel.setError("Empty field!")
            }
        }

    }

    fun checkFields(aantalDijkels: EditText, redenDijkel: EditText): Boolean {
        //return !(voornaam.isNullOrEmpty() || achternaam.isNullOrEmpty() || voornaam.isNullOrBlank() || achternaam.isNullOrBlank())
        var aantal: Int = aantalDijkels.text.toString().trim().toInt()
        var reden: String = redenDijkel.text.toString().trim()
        return aantal > 0 && reden != ""
    }

    fun onCheckedDone() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /*fun onCheckedDone() {

        if (checkbox.isChecked) {
            imgApproved.visibility = View.VISIBLE
        } else {
            imgApproved.visibility = View.INVISIBLE
        }
    }*/


}
