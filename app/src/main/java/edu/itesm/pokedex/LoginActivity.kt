package edu.itesm.pokedex

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.itesm.pokedex.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var bind : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Inicializa objetos:
        auth = Firebase.auth
        setLoginRegister()
    }

    override fun onStart(){
        super.onStart()
        val usuarioActivo = auth.currentUser
        if (usuarioActivo != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun setLoginRegister(){
        bind.registerbtn.setOnClickListener {
            if (bind.correo.text.isNotEmpty() && bind.password.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    bind.correo.text.toString(), //usuario y password
                    bind.password.text.toString()
                ).addOnCompleteListener{
                    if(it.isSuccessful){
                        usuarioCreado()
                        bind.correo.text.clear()
                        bind.password.text.clear()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        bind.loginbtn.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                bind.correo.text.toString(),
                bind.password.text.toString()
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"¡A Agregar Cameos!", Toast.LENGTH_LONG).show()
                    val intento = Intent(this, MainActivity::class.java)
                    startActivity(intento)
                    finish()
                }else{
                    Toast.makeText(this,"No se pudo Acceder", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun usuarioCreado(){
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle("usuario cameo")
            setMessage("Usuario creado con éxito!")
            setPositiveButton("Ok",null)
            show()
        }
    }
}