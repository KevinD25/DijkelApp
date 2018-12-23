package com.davis.kevin.dijkelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import com.davis.kevin.dijkelapp.Adapters.UserLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Hashing
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.davis.kevin.dijkelapp.DOM.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_praesidium.*
import kotlinx.android.synthetic.main.activity_schachten.*
import kotlinx.android.synthetic.main.custompraesidiumdialog.*
import java.security.MessageDigest

class PraesidiumActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var customView: View
    private lateinit var reference: DatabaseReference
    private var user: User? = null
    private var userLijst: MutableList<User> = mutableListOf()
    lateinit var item: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_praesidium)
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("users")
        fireBaseGet()
        listPraesidiumAdmin.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // This is your listview's selected item
            item = parent.getItemAtPosition(position) as User
            openEditActivity()
        }
    }

    private fun openEditActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                /* if (schachtenLijst[0].id == "1337") {
                     schachtenLijst.clear()
                 }*/
                if (dataSnapshot.exists()) {
                    userLijst.clear()
                    for (h in dataSnapshot.children) {
                        user = h.getValue(User::class.java)
                        userLijst.add(user!!)
                    }
                    val adapter = UserLijstAdapter(applicationContext, userLijst)
                    listPraesidiumAdmin.adapter = adapter
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
            customView(R.layout.custompraesidiumdialog)
        }

        // Setup custom view content
        customView = dialog.getCustomView() ?: return
        val voornaamInput: EditText = customView.findViewById(R.id.txtVoornaam)
        val achternaamInput: EditText = customView.findViewById(R.id.txtAchternaam)
        val wachtwoordInput: EditText = customView.findViewById(R.id.txtpassword)
        val wachtwoord2Input: EditText = customView.findViewById(R.id.txtpassword2)
        val rolInput: EditText = customView.findViewById(R.id.txtRol)
        val buttonConfirm: Button = customView.findViewById(R.id.btnConfirm)
        val buttonCancel: Button = customView.findViewById(R.id.btnCancel)

        buttonConfirm.setOnClickListener {
            onClickConfirm(voornaamInput, achternaamInput, wachtwoordInput, wachtwoord2Input, rolInput)
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun onClickConfirm(
        voornaamInput: EditText,
        achternaamInput: EditText,
        wachtwoordInput: EditText,
        wachtwoord2Input: EditText,
        rolInput: EditText
    ) {

        var vn: String = voornaamInput.text.toString().trim()
        var an: String = achternaamInput.text.toString().trim()
        var ww: String = wachtwoordInput.text.toString().trim()
        var ww2: String = wachtwoord2Input.text.toString().trim()
        var rol: String = rolInput.text.toString().trim()
        if (checkFields(vn, an, ww, ww2)) {

            val myRef = database.getReference("users")
            val praesidiumId = myRef.push().key.toString()
            var username = vn + " " + an
            username = username.replace(" ", ".")
            val hash = Hashing()
            ww = hash.hashPassword(ww)
            val user = User(praesidiumId, username, rol, ww)
            myRef.child(praesidiumId).setValue(user).addOnCompleteListener {
                //TODO CLOSE DIALOG
            }


        } else {
            Toast.makeText(this, "Vul alle velden in...", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkFields(voornaam: String, achternaam: String, ww: String, ww2: String): Boolean {
        //return !(voornaam.isNullOrEmpty() || achternaam.isNullOrEmpty() || voornaam.isNullOrBlank() || achternaam.isNullOrBlank())
        if (ww != ww2) {
            txtpassword.error = "Wachtwoorden komen niet overeen..."
            txtpassword2.error = "Wachtwoorden komen niet overeen..."
        }
        return voornaam != "" && achternaam != "" && ww != "" && ww2 != ""
    }




}
