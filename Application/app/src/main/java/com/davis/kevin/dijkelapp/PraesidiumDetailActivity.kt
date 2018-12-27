package com.davis.kevin.dijkelapp

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.davis.kevin.dijkelapp.Adapters.UserLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.MyApplication
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.davis.kevin.dijkelapp.DOM.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_praesidium_detail.*
import kotlinx.android.synthetic.main.activity_schacht_detail.*

class PraesidiumDetailActivity : AppCompatActivity() {

    lateinit var item: User
    private var id: String = ""
    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var user: User? = null
    private var userLijst: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_praesidium_detail)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        reference = FirebaseDatabase.getInstance().getReference("users")
        id = intent.getStringExtra("id")
        fireBaseGet()


    }

    private fun checkLoggedInUser() {
        if(currentUser.username != item.username){
            btnDelete2.isEnabled = true
            btnDelete2.setTextColor(Color.WHITE)
        }
        else{
            btnDelete2.isEnabled = false
            btnDelete2.setTextColor(Color.GRAY)
        }
        if(currentUser.username == "God"){
            btnEdit2.isEnabled = false
            btnEdit2.setTextColor(Color.GRAY)
        }
        else{
            btnEdit2.isEnabled = true
            btnEdit2.setTextColor(Color.WHITE)
        }
    }

    private fun checkRol() {
        spnRol.isEnabled = currentUser.role == "God" || currentUser.role == "god" || currentUser.role == "Praeses"
        if(item.role == "God" || item.role == "god"){
            spnRol.isEnabled = false
        }
    }

    fun onClickEdit(view: View) {
        val voornaam = txtVoornaam.text.toString().trim()
        val achternaam = txtAchternaam.text.toString().trim()
        var rol: String = spnRol.selectedItem.toString()
        var username = voornaam + " " + achternaam
        username = username.replace(" ", ".")

        if (voornaam.isEmpty()) editVoornaam.error = "Empty field"
        if (achternaam.isEmpty()) editAchternaam.error = "Empty field"

        if (voornaam.isNotEmpty() && achternaam.isNotEmpty()) {
            val user = User(item.id, username, rol, item.password)
            reference.child(item.id).setValue(user)
            super.onBackPressed()
        }
    }

    fun onClickDelete(view: View) {
        reference.child(item.id).setValue(null)
        super.onBackPressed()
    }

    fun fillSpinner() {
        // Create an ArrayAdapter
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.roles, android.R.layout.simple_spinner_item
        )

        when (MyApplication.currentUser.role) {
            "Vice-Praeses" -> adapter.remove("Preases")
            "Schachtenmeester" -> {
                adapter.remove("Praeses")
                adapter.remove("Vice-Praeses")
            }
            "Schachtentemmer" -> {
                adapter.remove("Praeses")
                adapter.remove("Vice-Praeses")
                adapter.remove("Schachtenmeester")
            }
            "Lid" -> {
                adapter.remove("Praeses")
                adapter.remove("Vice-Praeses")
                adapter.remove("Schachtenmeester")
                adapter.remove("Schachtentemmer")
                adapter.remove("lid")
            }
        }

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spnRol.adapter = adapter
        spnRol.setSelection(adapter.getPosition(item.role))
    }

    fun fillInfo() {
        val username = item.username.toString()
        val splitsen = username.split(".")
        val voornaam = splitsen[0]
        var achternaam: String = ""
        for (i in 1..splitsen.size - 1) {
            achternaam = achternaam + splitsen[i] + " "
        }
        achternaam = achternaam.trim()

        txtPraesidium.setText(voornaam + " " + achternaam)
        txtVoornaam.setText(voornaam)
        txtAchternaam.setText(achternaam)
        fillSpinner()
    }

    fun getClickedUser() {
        for (user in userLijst) {
            if (user.id == id) {
                item = user
            }
        }
        fillInfo()
        checkRol()
    }

    fun fireBaseGet() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    userLijst.clear()
                    for (h in dataSnapshot.children) {
                        user = h.getValue(User::class.java)
                        userLijst.add(user!!)
                    }
                    getClickedUser()
                    checkLoggedInUser()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(userListener)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
