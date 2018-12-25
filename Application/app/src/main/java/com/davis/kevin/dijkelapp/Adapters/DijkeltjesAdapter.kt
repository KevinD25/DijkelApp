package com.davis.kevin.dijkelapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.MyApplication.Companion.currentUser
import com.davis.kevin.dijkelapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DijkeltjesAdapter(
    private val context: Context,
    private val dataSource: MutableList<Dijkel>
) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var dijkelref: DatabaseReference = FirebaseDatabase.getInstance().getReference("dijkels")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.dijkeldetailcustom, parent, false)

        val DatumTextview = rowView.findViewById(R.id.txtDatum) as TextView
        val RedenTextview = rowView.findViewById(R.id.txtReden) as TextView
        val DonorTextview = rowView.findViewById(R.id.txtDonor) as TextView
        val checkbox = rowView.findViewById(R.id.chbDone) as CheckBox
        val image = rowView.findViewById(R.id.imgApproved) as ImageView
        //val DatumCheckedTextview = rowView.findViewById(R.id.txtDatumChecked) as TextView
        val donor = dataSource[position].donor
        DatumTextview.text = dataSource[position].datum
        RedenTextview.text = dataSource[position].reden
        DonorTextview.text = donor


        if (dataSource[position].done) {
            image.visibility = View.VISIBLE
            //checkbox.text = "checked by " + donor
        } else {
            image.visibility = View.INVISIBLE
            checkbox.text = "Done"
        }

        checkRoles(checkbox, position)

        checkbox.setOnClickListener {
            val isChecked = checkbox.isChecked()
            if (isChecked) {

                //checkbox.text = "checked by " + currentUser.username
                /*val sdf = SimpleDateFormat("dd/M/yyyy")
                val currentDate = sdf.format(Date())
                DatumCheckedTextview.text = currentDate
                DatumCheckedTextview.visibility = View.VISIBLE*/
                editDijkelState(position, checkbox, image)
            } else {
                editDijkelState(position, checkbox, image)
                image.visibility = View.INVISIBLE
                checkbox.text = "Done"
                // DatumCheckedTextview.visibility = View.INVISIBLE
            }
        }



        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    private fun editDijkelState(position: Int, checkbox: CheckBox, image:ImageView) {

                val dijkel = Dijkel(
                    dataSource[position].id,
                    dataSource[position].schachtid,
                    dataSource[position].reden,
                    dataSource[position].donor,
                    !dataSource[position].done,
                    dataSource[position].datum
                )
                dijkelref.child(dijkel.id).setValue(dijkel)
                if (checkbox.isChecked) {
                    image.visibility = View.VISIBLE
                }
                if (!checkbox.isChecked) {
                    image.visibility = View.INVISIBLE
                }
            }


    fun checkRoles(checkbox: CheckBox, position: Int) {
        when (currentUser.role) {
            "God" -> {
                checkbox.visibility = View.VISIBLE
            }
            "Praeses" -> {
                checkbox.visibility = View.VISIBLE
            }
            "Vice-Praeses" -> {
                checkbox.visibility = View.VISIBLE
            }
            "Schachtenmeester" -> {
                checkbox.visibility = View.VISIBLE
            }
            "Schachtentemmer" -> {
                checkbox.visibility = View.VISIBLE
            }
            "Lid" -> checkbox.visibility = View.INVISIBLE
        }

        checkbox.isChecked = dataSource[position].done == true
    }
}