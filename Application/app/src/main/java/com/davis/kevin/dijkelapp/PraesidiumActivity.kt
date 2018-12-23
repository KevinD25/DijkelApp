package com.davis.kevin.dijkelapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.Adapters.SchachtenLijstAdapter
import com.davis.kevin.dijkelapp.Adapters.UserLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Hashing
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
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
    private var tempUserLijst: MutableList<User> = mutableListOf()
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
        val intent = Intent(this, PraesidiumDetailActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    userLijst.clear()
                    for (h in dataSnapshot.children) {
                        user = h.getValue(User::class.java)
                        userLijst.add(user!!)
                    }
                    filterLijst(userLijst)
                    val adapter = UserLijstAdapter(applicationContext, userLijst)
                    listPraesidiumAdmin.adapter = adapter
                }
            }

            private fun filterLijst(userLijst: MutableList<User>) {
                tempUserLijst.clear()
                for(loop in userLijst){
                    tempUserLijst.add(loop)
                }
                for (loop in userLijst) {
                    when (currentUser.role) {
                        "Praeses" -> if (loop.role.equals("God")) {
                            tempUserLijst.remove(loop)
                        }
                        "Vice-Praeses" -> if (loop.role.equals("God") || loop.role.equals("Praeses")) {
                            tempUserLijst.remove(loop)
                        }
                        "Schachtenmeester" -> if (loop.role.equals("God") || loop.role.equals("Praeses") || loop.role.equals("Vice-Praeses")) {
                            tempUserLijst.remove(loop)
                        }
                        "Schachtentemmer" -> if (loop.role.equals("God") || loop.role.equals("Praeses") || loop.role.equals("Vice-Praeses") || loop.role.equals("Schachtenmeester")) {
                            tempUserLijst.remove(loop)
                        }
                        "Lid" -> if (loop.role.equals("God") || loop.role.equals("Praeses") || loop.role.equals("Vice-Praeses") || loop.role.equals(
                                "Schachtenmeester"
                            ) || loop.role.equals("Schachtentemmer")
                        ) {
                            tempUserLijst.remove(loop)
                        }
                    }
                }
                userLijst.clear()
                for(loop in tempUserLijst){
                    userLijst.add(loop)
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
        val rolInput: Spinner = customView.findViewById(R.id.spnRol)
        val buttonConfirm: Button = customView.findViewById(R.id.btnConfirm)
        val buttonCancel: Button = customView.findViewById(R.id.btnCancel)
        val spinner: Spinner = customView.findViewById(R.id.spnRol)

        // Create an ArrayAdapter
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.roles, android.R.layout.simple_spinner_item
        )

        when (currentUser.role) {
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
        spinner.adapter = adapter

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
        rolInput: Spinner
    ) {

        var vn: String = voornaamInput.text.toString().trim()
        var an: String = achternaamInput.text.toString().trim()
        var ww: String = wachtwoordInput.text.toString().trim()
        var ww2: String = wachtwoord2Input.text.toString().trim()
        var rol: String = rolInput.selectedItem.toString()
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
