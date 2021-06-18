package com.example.firebasecarsexplorer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasecarsexplorer.R
import com.example.firebasecarsexplorer.data.Car

class CarAdapter(private val listener: OnCarItemLongClick) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    private val carList = ArrayList<Car>()

    fun setCar(list: List<Car>){
        carList.clear()
        carList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeCar(car: Car, position: Int){
        carList.remove(car)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_row_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val name = holder.itemView.findViewById<TextView>(R.id.carName)
        val productionYear = holder.itemView.findViewById<TextView>(R.id.carProductionYear)
        val image = holder.itemView.findViewById<ImageView>(R.id.carImage)

        name.text = carList[holder.adapterPosition].name
        productionYear.text = carList[holder.adapterPosition].productionYear

        Glide.with(holder.itemView)
            .load(carList[holder.adapterPosition].image)
            .into(image)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    inner class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnLongClickListener(){
                listener.onCarItemLongClick(carList[adapterPosition], adapterPosition)
                true
            }
        }
    }
}

interface OnCarItemLongClick{
    fun onCarItemLongClick(car: Car, position: Int)
}