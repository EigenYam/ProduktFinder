package com.example.produktfinder

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.io.*

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextSearch = findViewById(R.id.editTextSearch)
        databaseHelper = DatabaseHelper(this)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = editTextSearch.text.toString().trim()
                searchInDatabase(searchText)
            }
        })

        // Kopiere die Datenbank aus dem assets-Ordner, falls sie noch nicht existiert
        copyDatabaseFromAssets()
    }

    private fun searchInDatabase(searchText: String) {
        val db: SQLiteDatabase = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Categories WHERE category_name LIKE '%$searchText%'", null)
        // Verarbeite das Cursor-Ergebnis und zeige es in deiner App an
        // z.B. durch eine RecyclerView oder eine ListView
        // Beispiel: while (cursor.moveToNext()) { ... }
        cursor.close()
    }

    private fun copyDatabaseFromAssets() {
        val contextWrapper = ContextWrapper(applicationContext)
        val dbPath = contextWrapper.getDatabasePath("ProduktfinderDB.db")

        if (!dbPath.exists()) {
            try {
                val inputStream: InputStream = assets.open("databases/ProduktfinderDB.db")
                val outputStream: OutputStream = FileOutputStream(dbPath)

                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "ProduktfinderDB.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Hier kannst du die Datenbank initialisieren
        // Beispiel: Erstellen von Tabellen, Initialisierung von Daten usw.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Hier kannst du ein Upgrade der Datenbank durchführen, falls nötig
    }
}
