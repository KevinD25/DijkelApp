package com.davis.kevin.dijkelapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.view.View
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.DOM.User
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        checkActiveRole()
        checkIfLoggedIn()
    }

    override fun onStart() {
        checkIfLoggedIn()
        super.onStart()
    }

    fun checkActiveRole() {
        val klrDisabled : Int = Color.GRAY
        val klrEnabled : Int = Color.WHITE
        when (currentUser.role) {
            "God" -> updateButtons(true, true,  klrEnabled, klrEnabled)
            "Praeses" -> updateButtons(true, true,  klrEnabled, klrEnabled)
            "Vice-Praeses" -> updateButtons(true, true,  klrEnabled, klrEnabled)
            "Schachtenmeester" -> updateButtons(false, true,  klrDisabled, klrEnabled)
            "Schachtentemmer" -> updateButtons(false, true,  klrDisabled, klrEnabled)
            "Lid" -> updateButtons(false, false, klrDisabled, klrDisabled)
        }

        txtLoggedInAs.text = "Ingelogd als " + currentUser.username
    }

    fun updateButtons(users: Boolean, schachten: Boolean, colorUser : Int, colorSchacht : Int) {
        btnUsers.isEnabled = users
        btnSchachten.isEnabled = schachten
        btnUsers.setTextColor(colorUser)
        btnSchachten.setTextColor(colorSchacht)
    }

    fun goToSchachten(view: View) {
        val intent = Intent(this, SchachtenActivity::class.java)
        startActivity(intent)
    }

    fun goToUsers(view: View) {
        val intent = Intent(this, PraesidiumActivity::class.java)
        startActivity(intent)
    }

    fun goToPasswordChange(view:View){
        val intent = Intent(this, PasswordActivity::class.java)
        startActivity(intent)
    }

    fun Logout(view:View){
        val intent = Intent(this, LoginActivity::class.java)
        val logoutUser : User = User("","", "", "")
        currentUser = logoutUser
        startActivity(intent)
    }

    private fun checkIfLoggedIn() {
        if (currentUser.id != "") {
        }
        else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

}
