package com.davis.kevin.dijkelapp.Adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.davis.kevin.dijkelapp.R

class SchachtenLijstAdapter(
    private val context: Context,
    private val dataSource: MutableList<Schacht>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.customschachtenlijst, parent, false)

        val Schachttextview = rowView.findViewById(R.id.txtSchachtnaam) as TextView
        Schachttextview.text = dataSource[position].voornaam + " " + dataSource[position].achternaam


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
}