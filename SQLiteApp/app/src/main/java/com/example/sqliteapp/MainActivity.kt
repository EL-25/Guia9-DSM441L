package com.example.sqliteapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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
    private var listCategorias = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtIdDB = findViewById(R.id.txtIdDB)
        txtId = findViewById(R.id.txtId)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtCantidad = findViewById(R.id.txtCantidad)
        cmbCategorias = findViewById(R.id.cmbCategorias)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnBuscar = findViewById(R.id.btnBuscar)

        dbHelper = HelperDB(this)
        db = dbHelper!!.writableDatabase

        managerCategoria = Categoria(this)
        managerProductos = Productos(this)

        setSpinnerCategorias()

        btnAgregar!!.setOnClickListener(this)
        btnActualizar!!.setOnClickListener(this)
        btnEliminar!!.setOnClickListener(this)
        btnBuscar!!.setOnClickListener(this)
    }

    fun setSpinnerCategorias() {
        managerCategoria!!.insertValuesDefault()
        cursor = managerCategoria!!.showAllCategoria()
        listCategorias.clear()

        if (cursor != null && cursor!!.count > 0) {
            if (cursor!!.moveToFirst()) {
                do {
                    listCategorias.add(cursor!!.getString(1))
                } while (cursor!!.moveToNext())
            }
        }
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCategorias)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbCategorias!!.adapter = adaptador
    }

    override fun onClick(view: View) {
        val nombre: String = txtNombre!!.text.toString().trim()
        val precio: String = txtPrecio!!.text.toString().trim()
        val cantidad: String = txtCantidad!!.text.toString().trim()
        val categoria: String = cmbCategorias!!.selectedItem?.toString()?.trim() ?: ""
        val idproducto: String = txtId!!.text.toString().trim()

        if (db != null) {
            when (view) {
                btnAgregar -> {
                    if (vericarFormulario("insertar")) {
                        val idcategoria = managerCategoria!!.searchID(categoria)
                        managerProductos!!.addNewProducto(
                            idcategoria,
                            nombre,
                            precio.toDouble(),
                            cantidad.toInt()
                        )
                        Toast.makeText(this, "Producto agregado con éxito", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }
                }
                btnActualizar -> {
                    if (vericarFormulario("actualizar")) {
                        val idcategoria = managerCategoria!!.searchID(categoria)
                        managerProductos!!.updateProducto(
                            idproducto.toInt(),
                            idcategoria,
                            nombre,
                            precio.toDouble(),
                            cantidad.toInt()
                        )
                        Toast.makeText(this, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }
                }
                btnEliminar -> {
                    if (vericarFormulario("eliminar")) {
                        managerProductos!!.deleteProducto(idproducto.toInt())
                        Toast.makeText(this, "Producto eliminado con éxito", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }
                }
                btnBuscar -> {
                    if (vericarFormulario("buscar")) {
                        val c: Cursor? = managerProductos!!.searchProducto(idproducto.toInt())
                        if (c != null && c.moveToFirst()) {
                            // Columnas: 0: idproductos, 1: idcategoria, 2: descripcion, 3: precio, 4: cantidad
                            val idCat = c.getInt(1)
                            val desc = c.getString(2)
                            val prec = c.getDouble(3)
                            val cant = c.getInt(4)

                            txtNombre!!.setText(desc)
                            txtPrecio!!.setText(prec.toString())
                            txtCantidad!!.setText(cant.toString())

                            // Cargar nombre de categoría en el Spinner
                            val nombreCat = managerCategoria!!.searchNombre(idCat)
                            if (nombreCat != null) {
                                val pos = listCategorias.indexOf(nombreCat)
                                if (pos >= 0) cmbCategorias!!.setSelection(pos)
                            }
                            Toast.makeText(this, "Producto encontrado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "No se encontró ningún producto con el código $idproducto", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "No se puede conectar a la Base de Datos", Toast.LENGTH_LONG).show()
        }
    }

    private fun vericarFormulario(opc: String): Boolean {
        var notificacion = "Se han generado algunos errores, favor verifíquelos"
        var response = true

        val nombre = txtNombre!!.text.toString().trim()
        val precio = txtPrecio!!.text.toString().trim()
        val cantidad = txtCantidad!!.text.toString().trim()
        val idproducto = txtId!!.text.toString().trim()

        if (opc == "buscar" || opc == "eliminar") {
            if (idproducto.isEmpty()) {
                txtId!!.error = "Ingrese el código del producto"
                txtId!!.requestFocus()
                notificacion = "Debe ingresar el código del producto"
                response = false
            }
        } else if (opc == "insertar" || opc == "actualizar") {
            if (opc == "actualizar" && idproducto.isEmpty()) {
                txtId!!.error = "Ingrese el código para actualizar"
                txtId!!.requestFocus()
                notificacion = "Debe ingresar el código del producto"
                response = false
            }
            if (nombre.isEmpty()) {
                txtNombre!!.error = "Ingrese el nombre del producto"
                txtNombre!!.requestFocus()
                response = false
            }
            if (precio.isEmpty()) {
                txtPrecio!!.error = "Ingrese el precio del producto"
                txtPrecio!!.requestFocus()
                response = false
            }
            if (cantidad.isEmpty()) {
                txtCantidad!!.error = "Ingrese la cantidad inicial"
                txtCantidad!!.requestFocus()
                response = false
            }
        }

        if (!response) {
            Toast.makeText(this, notificacion, Toast.LENGTH_LONG).show()
        }
        return response
    }

    private fun limpiarCampos() {
        txtId!!.setText("")
        txtNombre!!.setText("")
        txtPrecio!!.setText("")
        txtCantidad!!.setText("")
        if (cmbCategorias!!.adapter.count > 0) cmbCategorias!!.setSelection(0)
    }
}
