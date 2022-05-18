package edu.itesm.pokedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CameoAdapter(private val data: MutableList<Cameo>?) : RecyclerView.Adapter<CameoAdapter.ViewHolder>()  {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view){

        fun bind(property: Cameo){
            val title = view.findViewById<TextView>(R.id.tvTitle)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val description = view.findViewById<TextView>(R.id.tvDescription)
            val universo = view.findViewById<TextView>(R.id.tvUniverso)

            title.text = property.nombre
            description.text = property.genero
            universo.text = property.universo.toString()

            Glide.with(view.context)
                    .load(property.foto)
                    .circleCrop()
                    .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cameo_renglon, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data!![position])
    }

    fun deleteItem(index:Int){
        data?.removeAt(index)
        notifyDataSetChanged()
    }

    fun getItem(index: Int): Cameo? {
        return data?.get(index)
    }

}