package edu.itesm.pokedex

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.itesm.pokedex.databinding.ActivityCameoBinding


abstract class SwipeToDelete(context: Context, direccion: Int, direccionArrastre: Int):
        ItemTouchHelper.SimpleCallback(direccion, direccionArrastre){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}

class CameoActivity : AppCompatActivity() {

    private lateinit var bind: ActivityCameoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityCameoBinding.inflate(layoutInflater)
        setContentView(bind.root)
        cargaDatos()
    }

    private fun borraCameo(cameo:Cameo){
        val storage = FirebaseStorage.getInstance().getReferenceFromUrl(cameo.foto)
        storage.delete().addOnSuccessListener {
            val usuario = Firebase.auth.currentUser
            val referencia = FirebaseDatabase.getInstance().getReference("cameo/${usuario.uid}/${cameo.id}")
            referencia.removeValue()
        }
    }

    private fun cargaDatos() {
        var reference: DatabaseReference
        var database: FirebaseDatabase
        database = FirebaseDatabase.getInstance()
        val usuario = Firebase.auth.currentUser
        reference = database.getReference("cameo/${usuario.uid}")

        bind.recycler.apply {
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listaCameos = ArrayList<Cameo>()
                    for (cameo in snapshot.children) {
                        var objeto = cameo.getValue(Cameo::class.java)
                        listaCameos.add(objeto as Cameo)
                    }
                    if(listaCameos.isEmpty()){
                        Toast.makeText(this@CameoActivity, "Error al obtener datos", Toast.LENGTH_LONG).show()
                    }
                    adapter = CameoAdapter(listaCameos)
                    layoutManager = LinearLayoutManager(this@CameoActivity)

                    val item = object : SwipeToDelete(this@CameoActivity,
                        ItemTouchHelper.UP, ItemTouchHelper.LEFT){
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            super.onSwiped(viewHolder, direction)
                            val cameo = listaCameos[viewHolder.adapterPosition]
                            borraCameo(cameo)
                        }
                    }
                    val itemTouchHelper = ItemTouchHelper(item)
                    itemTouchHelper.attachToRecyclerView(bind.recycler)

                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CameoActivity, "Error al obtener datos", Toast.LENGTH_LONG).show()
                }
            })

        }
    }


}