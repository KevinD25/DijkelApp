package com.davis.kevin.dijkelapp.DOM

import android.app.Application

class MyApplication : Application() {
    companion object {
        var currentUser : User = User("", "", "", "")
    }
}