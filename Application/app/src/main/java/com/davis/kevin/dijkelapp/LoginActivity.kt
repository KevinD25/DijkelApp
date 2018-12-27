package com.davis.kevin.dijkelapp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.davis.kevin.dijkelapp.Adapters.UserLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Hashing
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.DOM.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_praesidium.*

class LoginActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var user: User? = null
    private var userLijst: MutableList<User> = mutableListOf()
    private lateinit var activeuser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        checkIfLoggedIn()

        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("users")
        fireBaseGet()


        txtSignIn.setOnClickListener {

            //DEVELOPMENT STAGE
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            //DELETE UPPER TO ENABLE SIGN-IN

            val hash = Hashing()
            val username: String = editUsername.text.toString().trim()
            var password: String = editPassword.text.toString().trim()
            if (!username.equals("") && !password.equals("")) {
                password = hash.hashPassword(password)
                if (checkCredentials(username, password))
                    SignIn(username, password)
                else {
                    editUsername.error = "Invalid credentials"
                    editPassword.error = "Invalid credentials"
                }
            } else {
                if (username.equals(""))
                    editUsername.setError("username is blank!")
                if (password.equals(""))
                    editPassword.setError("password is blank!")
            }
        }
    }

    override fun onStart() {
        checkIfLoggedIn()
        super.onStart()
    }

    private fun checkIfLoggedIn() {
        if (currentUser.id != "") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        super.onBackPressed()
    }

    fun checkCredentials(username: String, password: String): Boolean {
        var match: Boolean = false
        for (item in userLijst) {
            if (item.username.toLowerCase() == username.toLowerCase() && item.password == password) {
                activeuser = item
                match = true
            }
        }
        return match
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

    fun SignIn(email: String, password: String) {
        currentUser = activeuser
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
