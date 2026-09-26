package com.example.sqliteapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.db.HelperDB
import com.example.sqliteapp.model.Categoria
import com.example.sqliteapp.model.Productos
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var managerCategoria: Categoria? = null
    private var managerProductos: Productos? = null
    private var dbHelper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private var txtIdDB: TextView? = null
    private var txtId: EditText? = null
    private var txtNombre: EditText? = null
    private var txtPrecio: EditText? = null
    private var txtCantidad: EditText? = null
    private var cmbCategorias: Spinner? = null
    private var btnAgregar: Button? = null
    private var btnActualizar: Button? = null
    private var btnEliminar: Button? = null
    private var btnBuscar: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtIdDB = findViewById(R.id.txtIdDB)
        txtId = findViewById(R.id.txtId)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtCantidad = findViewById(R.id.txtCantidad)
        cmbCategorias = findViewById<Spinner>(R.id.cmbCategorias)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnBuscar = findViewById(R.id.btnBuscar)
        dbHelper = HelperDB(this)
        db = dbHelper!!.writableDatabase
        setSpinnerCategorias()
        btnAgregar!!.setOnClickListener(this)
        btnActualizar!!.setOnClickListener(this)
        btnEliminar!!.setOnClickListener(this)
        btnBuscar!!.setOnClickListener(this)
    }

    fun setSpinnerCategorias() {
// Cargando valores por defecto
        managerCategoria = Categoria(this)
        managerCategoria!!.insertValuesDefault()
        cursor = managerCategoria!!.showAllCategoria()
        var cat = ArrayList<String>()
        if (cursor != null && cursor!!.count > 0) {
            cursor!!.moveToFirst()
            cat.add(cursor!!.getString(1))
            do {
                cat.add(cursor!!.getString(1))
            } while (cursor!!.moveToNext())
        }
        var adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, cat)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbCategorias!!.adapter = adaptador
    }

    override fun onClick(view: View) {
        managerProductos = Productos(this)
        val nombre: String = txtNombre!!.text.toString().trim()
        val precio: String = txtPrecio!!.text.toString().trim()
        val cantidad: String = txtCantidad!!.text.toString().trim()
        val categoria: String = cmbCategorias!!.selectedItem.toString().trim()
        val idcategoria = managerCategoria!!.searchID(categoria)
        val idproducto = txtId!!.text.toString().trim()
        if (db != null) {
            if (view === btnAgregar) {
                if (vericarFormulario("insertar")) {
                    managerProductos!!.addNewProducto(
                        idcategoria,
                        nombre,
                        precio.toDouble(),
                        cantidad.toInt()
                    )
                    Toast.makeText(
                        this, "Producto agregado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (view === btnActualizar) {
                if (vericarFormulario("actualizar")) {
                    managerProductos!!.updateProducto(
                        idproducto.toInt(),
                        idcategoria,
                        nombre,
                        precio.toDouble(),
                        cantidad.toInt()
                    )
                    Toast.makeText(
                        this, "Producto actualizado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (view === btnEliminar) {
                if (vericarFormulario("eliminar")) {
// manager.eliminar(1);
                    managerProductos!!.deleteProducto(idproducto.toInt())
                    Toast.makeText(
                        this, "Producto eliminado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (view === btnBuscar) {
                /*IMPLEMENTE LA BUSQUEDA*/
            } else {
                Toast.makeText(
                    this, "No se puede conectar a la Base de Datos",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun vericarFormulario(opc: String): Boolean {
        var notificacion: String = "Se han generado algunos errores, favor verifiquelos"
        var response = true
        var idproducto_v = true
        var idcategoria_v = true
        var nombre_v = true
        var precio_v = true
        var cantidad_v = true
        val nombre: String = txtNombre!!.text.toString().trim()
        val precio: String = txtPrecio!!.text.toString().trim()
        val cantidad: String = txtCantidad!!.text.toString().trim()
        val categoria: String = cmbCategorias!!.selectedItem.toString().trim()
        val idproducto: String = txtId!!.text.toString().trim()
        if (opc === "insertar" || opc == "actualizar") {
            if (nombre.isEmpty()) {
                txtNombre!!.error = "Ingrese el nombre del producto"
                txtNombre!!.requestFocus()
                nombre_v = false
            }
            if (precio.isEmpty()) {
                txtPrecio!!.error = "Ingrese el precio del producto"
                txtPrecio!!.requestFocus()
                precio_v = false
            }
            if (cantidad.isEmpty()) {
                txtCantidad!!.error = "Ingrese la cantidad inicial"
                txtCantidad!!.requestFocus()
                cantidad_v = false
            }
            if (opc == "actualizar") {
                if (idproducto.isEmpty()) {
                    idproducto_v = false
                    notificacion = "No se ha seleccionado un producto"
                }
                response =
                    !(nombre_v == false || precio_v == false || cantidad_v == false ||
                            idproducto_v == false)
            } else {
                response = !(nombre_v == false || precio_v == false || cantidad_v ==
                        false)
            }
        } else if (opc === "eliminar" || opc == "buscar") {
            if (idproducto.isEmpty()) {
                response = false
                notificacion = "No se ha seleccionado un producto"
            }
        }
//Mostrar errores
        if (response == false) {
            Toast.makeText(
                this,
                notificacion,
                Toast.LENGTH_LONG
            ).show()
        }
        return response
    }
}