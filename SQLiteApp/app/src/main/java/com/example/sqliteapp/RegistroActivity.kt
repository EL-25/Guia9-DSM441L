package com.example.sqliteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.model.Usuario

class RegistroActivity : AppCompatActivity() {
    private var managerUsuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        managerUsuario = Usuario(this)

        val txtUser = findViewById<EditText>(R.id.txtUserReg)
        val txtPass = findViewById<EditText>(R.id.txtPassReg)
        val btnGuardar = findViewById<Button>(R.id.btnRegistrarGuardar)

        btnGuardar.setOnClickListener {
            val user = txtUser.text.toString().trim()
            val pass = txtPass.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val exito = managerUsuario!!.registrarUsuario(user, pass)
                if (exito) {
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
