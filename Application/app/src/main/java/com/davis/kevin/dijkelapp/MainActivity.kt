package com.davis.kevin.dijkelapp

import android.content.Intent
import android.content.pm.ActivityInfo
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


    //songs
    private lateinit var gravensteen : MediaPlayer
    private lateinit var blauwvoet : MediaPlayer
    private lateinit var zeeroverslied : MediaPlayer
    private lateinit var stormopzee : MediaPlayer
    private lateinit var countryroads : MediaPlayer
    private lateinit var vlaamseleeuw : MediaPlayer
    private lateinit var kutschacht : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")

        gravensteen = MediaPlayer.create(this, R.raw.gravensteen)
        blauwvoet = MediaPlayer.create(this, R.raw.blauwvoet)
        zeeroverslied = MediaPlayer.create(this, R.raw.zeeroverslied)
        stormopzee = MediaPlayer.create(this, R.raw.stormopzee)
        countryroads = MediaPlayer.create(this, R.raw.countryroads)
        vlaamseleeuw = MediaPlayer.create(this, R.raw.vlaamseleeuw)
        kutschacht = MediaPlayer.create(this, R.raw.kutschacht)

        fireBaseGet()

        initialize()
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    private fun initialize(){
        schachtenLijst.sortBy { it.voornaam }
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
        super.onResume()
        initialize()
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
                    schachtenLijst.sortBy { it.voornaam }
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

                var song : String = charSequence.toString()

                when(song){
                    "gravensteen" -> {
                        startSong(gravensteen)
                    }
                    "blauwvoet" -> {
                        startSong(blauwvoet)
                    }
                    "zeeroverslied" -> {
                        startSong(zeeroverslied)
                    }
                    "storm op zee" -> {
                        startSong(stormopzee)
                    }
                    "country roads" -> {
                        startSong(countryroads)
                    }
                    "vlaamse leeuw" -> {
                        startSong(vlaamseleeuw)
                    }
                    "kutschacht" -> {
                        val adapter = DijkelLijstAdapter(applicationContext, schachtenLijst, dijkelLijst)
                        listSchachten.adapter = adapter
                        startSong(kutschacht)
                        kutschacht.isLooping = false
                    }
                    else -> {
                        if(gravensteen.isPlaying ()){
                            stopSong(gravensteen)
                        }
                        if(blauwvoet.isPlaying ()){
                            stopSong(blauwvoet)
                        }
                        if(zeeroverslied.isPlaying ()){
                            stopSong(zeeroverslied)
                        }
                        if(stormopzee.isPlaying ()){
                            stopSong(stormopzee)
                        }
                        if(countryroads.isPlaying ()){
                            stopSong(countryroads)
                        }
                        if(vlaamseleeuw.isPlaying()){
                            stopSong(vlaamseleeuw)
                        }
                    }
                }

               /* if(charSequence.toString().equals("gravensteen")){
                    mediaPlayer = MediaPlayer.create(me, R.raw.gravensteen)
                    mediaPlayer.start()
                    mediaPlayer.isLooping = true
                }
                else{
                    if (mediaPlayer.isPlaying ()) {
                        mediaPlayer.pause ()
                        mediaPlayer.seekTo(0)
                        mediaPlayer.isLooping = false
                    }

                }*/
            }

            override fun afterTextChanged(editable: Editable) {
                Log.d("LOG_TAG", javaClass.simpleName + " text changed " + searchBar.text)
            }

        })
    }

    fun stopSong(mediaPlayer: MediaPlayer){
        mediaPlayer.pause ()
        mediaPlayer.seekTo(0)
        mediaPlayer.isLooping = false
    }

    fun startSong(mediaPlayer: MediaPlayer){
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    override fun onDestroy () {
        super.onDestroy ()
        gravensteen.release ()
        blauwvoet.release ()
        zeeroverslied.release ()
        stormopzee.release ()
        countryroads.release ()
    }

    fun reset(){
        searchBar.text = ""
    }
}
