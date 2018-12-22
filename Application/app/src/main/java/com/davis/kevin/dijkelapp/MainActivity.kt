package com.davis.kevin.dijkelapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.davis.kevin.dijkelapp.Adapters.DijkelLijstAdapter
import com.davis.kevin.dijkelapp.Adapters.DijkeltjesAdapter
import com.davis.kevin.dijkelapp.Adapters.SchachtenLijstAdapter
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dijkel.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_schachten.*

class MainActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var schacht: Schacht? = null
    private var schachtenLijst: MutableList<Schacht> = mutableListOf()
    lateinit var item : Schacht
    private var dijkel: Dijkel? = null
    private var dijkelLijst: MutableList<Dijkel> = mutableListOf()
    private lateinit var dijkelref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("schachten")
        dijkelref = FirebaseDatabase.getInstance().getReference("dijkels")

        fireBaseGet()
        val adapter = DijkelLijstAdapter(applicationContext, schachtenLijst, dijkelLijst)
        listSchachten.adapter = adapter
        listSchachten.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // This is your listview's selected item
             item = parent.getItemAtPosition(position) as Schacht
            openDijkelDetail()
        }
    }

    override fun onResume() {
        fireBaseGet()
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
    }

    fun openDijkelDetail(){
        val intent = Intent(this, DijkelActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
    }

    fun openDijkelDialog(v: View) {
        val parentRow = v.parent as View
        val listView = parentRow.parent as ListView
        val position = listView.getPositionForView(parentRow)

        val naam =  schachtenLijst[position].voornaam + " " + schachtenLijst[position].achternaam + " (" + schachtenLijst[position].dijkels + ")"


        val dialog = MaterialDialog(this)
            .customView(R.layout.customdijkeldialog, scrollable = true)

        val customView = dialog.getCustomView()
        // Use the view instance, e.g. to set values or setup listeners


        dialog.show()
    }
}
