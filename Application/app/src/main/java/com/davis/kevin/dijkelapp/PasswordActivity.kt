package com.davis.kevin.dijkelapp

import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.davis.kevin.dijkelapp.Adapters.UserLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Hashing
import com.davis.kevin.dijkelapp.DOM.MyApplication
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.DOM.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_password.*
import kotlinx.android.synthetic.main.activity_praesidium.*
import kotlinx.android.synthetic.main.custompraesidiumdialog.*

class PasswordActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var user: User? = null
    private var userLijst: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("users")
        fireBaseGet()

    }

    private fun veranderWachtwoord() {
        var oldPassword: String = txtOldpassword.text.toString().trim()
        val newPassword: String = txtNewPassword.text.toString().trim()
        val newPassword2: String = txtNewPassword2.text.toString().trim()
        checkCredentials(oldPassword, newPassword, newPassword2)
    }

    private fun checkCredentials(oldPw: String, newPw: String, newPw2: String) {

        val hash = Hashing()
        val checkWW: String = hash.hashPassword(oldPw)

        if (currentUser.password.equals(checkWW)) {
            if (newPw.equals(newPw2)) {
                val changePass = hash.hashPassword(newPw)
                val user = User(currentUser.id, currentUser.username, currentUser.role, changePass)
                Toast.makeText(this, "Password changed...", Toast.LENGTH_SHORT).show()
                reference.child(currentUser.id).setValue(user)
                super.onBackPressed()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                txtNewPassword.error = "Does not match..."
                txtNewPassword2.error = "Does not match..."
            }
        } else {
            txtOldpassword.error = "Wrong password"
        }
    }

    fun onClickConfirm(view: View) {
        veranderWachtwoord()
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
