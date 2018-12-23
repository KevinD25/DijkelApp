package com.davis.kevin.dijkelapp

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
        reference = FirebaseDatabase.getInstance().getReference("users")
        id = intent.getStringExtra("id")
        fireBaseGet()
        checkRol()
    }

    private fun checkRol() {
        spnRol.isEnabled = currentUser.role != "God"
    }

    fun onClickEdit(view: View) {
        val voornaam = editVoornaam.text.toString().trim()
        val achternaam = editAchternaam.text.toString().trim()
        var rol: String = spnRol.selectedItem.toString()
        var username = voornaam + " " + achternaam
        username = username.replace(" ", ".")

        if (voornaam.isEmpty()) editVoornaam.error = "Empty field"
        if (achternaam.isEmpty()) editAchternaam.error = "Empty field"

        if (voornaam.isNotEmpty() && achternaam.isNotEmpty()) {
            val user = User(item.id, username, item.password, rol)
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
            achternaam = splitsen[i] + " "
        }
        achternaam = achternaam.trim()

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
}
