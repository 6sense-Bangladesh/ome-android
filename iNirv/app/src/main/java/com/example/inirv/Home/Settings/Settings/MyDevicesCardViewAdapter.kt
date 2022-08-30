package com.example.inirv.Home.Settings.Settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inirv.Knob.Knob
import com.example.inirv.R

class MyDevicesCardViewAdapter(
    private val mList: List<Knob>
    ): RecyclerView.Adapter<MyDevicesCardViewAdapter.ViewHolder>() {

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.my_devices_card_view_image_view)
        val textView: TextView = itemView.findViewById(R.id.my_devices_card_view_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_my_devices, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val knob = mList[position]
        holder.textView.text = "Knob #${knob.mStovePosition}"

//        holder.imageView.setImageResource(R.drawable.ic_drop_down_arrow)
//        holder.imageView.rotationX = 180F
    }

    override fun getItemCount(): Int {

        return mList.size
    }
}