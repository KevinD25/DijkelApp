package com.davis.kevin.dijkelapp

import android.content.Intent
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
    }

    fun checkActiveRole() {
        when (currentUser.role) {
            "god" -> updateButtons(true, true)
            "Praeses" -> updateButtons(true, true)
            "Vice-Praeses" -> updateButtons(true, true)
            "Schachtenmeester" -> updateButtons(false, true)
            "Schachtentemmer" -> updateButtons(false, true)
            "Lid" -> updateButtons(false, false)
        }

        txtLoggedInAs.text = "Ingelogd als " + currentUser.username
    }

    fun updateButtons(users: Boolean, schachten: Boolean) {
        btnUsers.isEnabled = users
        btnSchachten.isEnabled = schachten
    }

    fun goToSchachten(view: View) {
        val intent = Intent(this, SchachtenActivity::class.java)
        startActivity(intent)
    }

    fun goToUsers(view: View) {
        val intent = Intent(this, PraesidiumActivity::class.java)
        startActivity(intent)
    }

    fun Logout(view:View){
        val intent = Intent(this, LoginActivity::class.java)
        val logoutUser : User = User("","", "", "")
        currentUser = logoutUser
        startActivity(intent)
    }

}
