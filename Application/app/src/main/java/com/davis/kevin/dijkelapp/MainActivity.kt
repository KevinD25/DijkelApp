package com.davis.kevin.dijkelapp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.davis.kevin.dijkelapp.Adapters.DijkelLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()
    lateinit var item : Schacht
    private var dijkel: Dijkel? = null
    private var dijkelLijst: MutableList<Dijkel> = mutableListOf()
    private lateinit var dijkelref: DatabaseReference
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")

        mediaPlayer = MediaPlayer.create(this, R.raw.gravensteen)

        fireBaseGet()
        val adapter = DijkelLijstAdapter(applicationContext, schachtenLijst, dijkelLijst)
        listSchachten.adapter = adapter
        listSchachten.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // This is your listview's selected item
             item = parent.getItemAtPosition(position) as Schacht
            openDijkelDetail()
        }
        searchbar()
        reset()

    }

    override fun onBackPressed() {
        if(currentUser.id != ""){
            moveTaskToBack(true)
        }
        super.onBackPressed()
    }

    override fun onResume() {
        fireBaseGet()
        reset()
        super.onResume()
    }

    fun fireBaseGet() {
        val schachtListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    schachtenLijst.clear()


                    for (h in dataSnapshot.children) {
                        schacht = h.getValue(Schacht::class.java)
                        schachtenLijst.add(schacht!!)
                    }
                    val adapter = DijkelLijstAdapter(applicationContext, schachtenLijst, dijkelLijst)
                    listSchachten.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        reference.addValueEventListener(schachtListener)


        val dijkelListener = object : ValueEventListener {
            override fun onDataChange(dijkelSnapshot: DataSnapshot) {

                // Get Post object and use the values to update the UI
                if (dijkelSnapshot.exists()) {
                    dijkelLijst.clear()
                    for (h in dijkelSnapshot.children) {
                        dijkel = h.getValue(Dijkel::class.java)
                        dijkelLijst.add(dijkel!!)
                    }
                    val adapter = DijkelLijstAdapter(applicationContext, schachtenLijst, dijkelLijst)
                    listSchachten.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        dijkelref.addValueEventListener(dijkelListener)
    }

    fun goToSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    fun openDijkelDetail(){
        val intent = Intent(this, DijkelActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    fun searchbar(){
        val searchbar = findViewById(R.id.searchBar) as MaterialSearchBar

        //Searchbar text change listener
        searchbar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                var filteredSchachtenLijst:MutableList<Schacht> = mutableListOf()
                schachtenLijst.filterTo(filteredSchachtenLijst, {it.getName().contains(charSequence)})
                val adapter = DijkelLijstAdapter(applicationContext, filteredSchachtenLijst, dijkelLijst)
                listSchachten.adapter = adapter

                if(charSequence.toString().equals("gravensteen")){
                    mediaPlayer.start()
                    mediaPlayer.isLooping = true
                }
                else{
                    if (mediaPlayer.isPlaying ()) {
                        mediaPlayer.pause ()
                        mediaPlayer.seekTo(0)
                        mediaPlayer.isLooping = false
                    }

                }
            }

            override fun afterTextChanged(editable: Editable) {
                Log.d("LOG_TAG", javaClass.simpleName + " text changed " + searchBar.text)
            }

        })
    }

    override fun onDestroy () {
        super.onDestroy ()
        mediaPlayer.release ()
    }

    fun reset(){
        searchBar.text = ""
    }
}
