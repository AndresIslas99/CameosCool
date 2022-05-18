package edu.itesm.pokedex

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var bundle: Bundle

    private val RICapture = 10007
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var foto: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()

        analytics = FirebaseAnalytics.getInstance(this)
        bundle = Bundle()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        activaReferencia()
    }

    private fun activaReferencia() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION),
                10007
            )
        }
    }

    public fun getCameo(view: View) {
        startActivity(Intent(this,CameoActivity::class.java))
    }

    public fun logout(view: View) {
        Firebase.auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    public fun fotoCameo(view: View) {
        val tomaFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(tomaFoto, RICapture)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RICapture && resultCode == RESULT_OK) {
            foto = data?.extras?.get("data") as Bitmap
        }
    }

    public fun addCameo(view: View) {
        val nombre = findViewById<EditText>(R.id.nombre).text
        val genero = findViewById<EditText>(R.id.genero).text
        val universo = findViewById<EditText>(R.id.universo).text

        if (nombre.isNotEmpty() && nombre.isNotBlank() && genero.isNotEmpty() && genero.isNotBlank() && universo.isNotEmpty() && universo.isNotBlank()) {
            val usuario = Firebase.auth.currentUser
            reference = database.getReference("cameo/${usuario.uid}")


            if (foto != null) {            // Convertir a bytes la foto
                val baos = ByteArrayOutputStream()
                foto.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val fileName = UUID.randomUUID().toString();
                val storage_reference = FirebaseStorage.getInstance().getReference("/cameofotos/${usuario.uid}/$fileName")
                val uploadTask = storage_reference.putBytes(data)

                uploadTask.addOnSuccessListener {
                    storage_reference.downloadUrl.addOnSuccessListener {
                        val id = reference.push().key
                        val came = Cameo(
                            id.toString(),
                            nombre.toString(),
                            genero.toString(),
                            universo.toString(),
                            it.toString()
                        )
                        reference.child(id!!).setValue(came)
                        nombre.clear()
                        genero.clear()
                        universo.clear()
                        Toast.makeText(this, "Cameo Registrado!", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al subir Cameo", Toast.LENGTH_LONG).show()
                }
            }
            bundle.putString("edu_itesm_pokedex_main", "added_pokedex")
            analytics.logEvent("main", bundle)
        } else {
            Toast.makeText(applicationContext, "error en nombre o tipo!", Toast.LENGTH_LONG).show()
        }
    }
}

