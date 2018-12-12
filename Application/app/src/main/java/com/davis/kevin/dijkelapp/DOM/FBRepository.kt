package com.davis.kevin.dijkelapp.DOM

import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class FBRepository : IRepository {
    private lateinit var repo : FBRepository
    protected lateinit var schachtCache: HashMap<Int, Schacht>
    protected var nextSchachtId: Int? = null
   // protected var userCache: HashMap<Int, User>
   // protected var nextUserId: Int? = null
/*
    fun getInstance(): FBRepository {
        if (repo == null) {
            repo = FBRepository
        }
        return repo
    }

    fun FBRepository(){
        val fbdb = FirebaseDatabase.getInstance()
    }*/
}