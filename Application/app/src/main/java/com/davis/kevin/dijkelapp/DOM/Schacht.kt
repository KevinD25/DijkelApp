package com.davis.kevin.dijkelapp.DOM

class Schacht(val id: String = "", val voornaam: String ="", val achternaam:String = "", val dijkels : Int = 0) {

    fun getName(): String {
        return voornaam.toLowerCase() + " " + achternaam.toLowerCase()
    }
}