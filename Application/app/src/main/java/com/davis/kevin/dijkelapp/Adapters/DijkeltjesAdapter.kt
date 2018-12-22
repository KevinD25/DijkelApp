package com.davis.kevin.dijkelapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.davis.kevin.dijkelapp.DOM.Dijkel
import com.davis.kevin.dijkelapp.DOM.Schacht
import com.davis.kevin.dijkelapp.R

class DijkeltjesAdapter(private val context: Context,
                        private val dataSource: MutableList<Dijkel>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.dijkeldetailcustom, parent, false)

        val DatumTextview = rowView.findViewById(R.id.txtDatum) as TextView
        val RedenTextview = rowView.findViewById(R.id.txtReden) as TextView
        val DonorTextview = rowView.findViewById(R.id.txtDonor) as TextView
        val checkbox =rowView.findViewById(R.id.chbDone) as CheckBox

        DatumTextview.text = dataSource[position].datum
        RedenTextview.text = dataSource[position].reden
        DonorTextview.text = dataSource[position].donor

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