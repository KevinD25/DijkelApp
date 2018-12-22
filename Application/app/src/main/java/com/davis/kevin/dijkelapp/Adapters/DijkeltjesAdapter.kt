package com.davis.kevin.dijkelapp.Adapters

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
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
        val image = rowView.findViewById(R.id.imgApproved) as ImageView
        val donor = dataSource[position].donor
        DatumTextview.text = dataSource[position].datum
        RedenTextview.text = dataSource[position].reden
        DonorTextview.text = donor

        if(dataSource[position].done){
            image.visibility = View.VISIBLE
            checkbox.text = "checked by " + donor
        }
        else{
            image.visibility = View.INVISIBLE
            checkbox.text = "Done"
        }

        checkbox.setOnClickListener {
                val isChecked = checkbox.isChecked()
                if(isChecked){
                    image.visibility = View.VISIBLE
                    checkbox.text = "checked by " + donor
                } else{
                    image.visibility = View.INVISIBLE
                    checkbox.text = "Done"
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
}