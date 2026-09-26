package com.example.sqliteapp.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.sqliteapp.db.HelperDB

class Usuario(context: Context?) {
    private var helper: HelperDB? = null
    private var db: SQLiteDatabase? = null

    init {
        helper = HelperDB(context)
        db = helper!!.writableDatabase
    }

    companion object {
        val TABLE_NAME_USUARIO = "usuario"
        val COL_ID = "idusuario"
        val COL_NICK = "nick"
        val COL_PASSWORD = "password"

        val CREATE_TABLE_USUARIO = (
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_USUARIO + "("
                        + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_NICK + " TEXT UNIQUE NOT NULL, "
                        + COL_PASSWORD + " TEXT NOT NULL);"
                )
    }

    fun registrarUsuario(nick: String, pass: String): Boolean {
        val valores = ContentValues().apply {
            put(COL_NICK, nick)
            put(COL_PASSWORD, pass)
        }
        val res = db!!.insert(TABLE_NAME_USUARIO, null, valores)
        return res != -1L
    }

    fun validarLogin(nick: String, pass: String): Boolean {
        val columns = arrayOf(COL_ID)
        val cursor: Cursor = db!!.query(
            TABLE_NAME_USUARIO,
            columns,
            "$COL_NICK=? AND $COL_PASSWORD=?",
            arrayOf(nick, pass),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }
}
