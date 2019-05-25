package guepardoapps.mynoteencrypted.database.note

import android.content.ContentValues
import android.content.Context
import guepardoapps.mynoteencrypted.model.Note
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial
// https://github.com/cbeust/kotlin-android-example/blob/master/app/src/main/kotlin/com/beust/example/DbHelper.kt

internal class DbNote(context: Context, private val password: String)
    : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId INTEGER PRIMARY KEY autoincrement,"
                        + "$ColumnTitle TEXT NOT NULL,"
                        + "$ColumnContent TEXT NOT NULL,"
                        + "$ColumnYear  INTEGER,"
                        + "$ColumnMonth  INTEGER,"
                        + "$ColumnHour  INTEGER,"
                        + "$ColumnDay  INTEGER,"
                        + "$ColumnMinute  INTEGER,"
                        + "$ColumnSecond  INTEGER"
                        + ")")
        database.execSQL(createTable)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    fun add(note: Note): Long {
        val values = ContentValues().apply {
            put(ColumnTitle, note.title)
            put(ColumnContent, note.content)
            put(ColumnYear, note.year)
            put(ColumnMonth, note.month)
            put(ColumnDay, note.day)
            put(ColumnHour, note.hour)
            put(ColumnMinute, note.minute)
            put(ColumnSecond, note.second)
        }

        return this.getWritableDatabase(password).insert(DatabaseTable, null, values)
    }

    fun update(note: Note): Int {
        val values = ContentValues().apply {
            put(ColumnTitle, note.title)
            put(ColumnContent, note.content)
            put(ColumnYear, note.year)
            put(ColumnMonth, note.month)
            put(ColumnDay, note.day)
            put(ColumnHour, note.hour)
            put(ColumnMinute, note.minute)
            put(ColumnSecond, note.second)
        }

        return this.getWritableDatabase(password).update(DatabaseTable, values, "$ColumnId LIKE ?", arrayOf(note.id.toString()))
    }

    fun delete(id: Int): Int = this.getWritableDatabase(password).delete(DatabaseTable, "$ColumnId LIKE ?", arrayOf(id.toString()))

    fun get(): MutableList<Note> {
        val cursor = this.getReadableDatabase(password).query(
                DatabaseTable, arrayOf(ColumnId, ColumnTitle, ColumnContent, ColumnYear, ColumnMonth, ColumnDay, ColumnHour, ColumnMinute, ColumnSecond),
                null, null, null, null, "$ColumnId ASC")

        val list = mutableListOf<Note>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(ColumnId))

                val title = getString(getColumnIndexOrThrow(ColumnTitle))
                val content = getString(getColumnIndexOrThrow(ColumnContent))

                val year = getInt(getColumnIndexOrThrow(ColumnYear))
                val month = getInt(getColumnIndexOrThrow(ColumnMonth))
                val day = getInt(getColumnIndexOrThrow(ColumnDay))

                val hour = getInt(getColumnIndexOrThrow(ColumnHour))
                val minute = getInt(getColumnIndexOrThrow(ColumnMinute))
                val second = getInt(getColumnIndexOrThrow(ColumnSecond))

                list.add(Note(id, title, content, year, month, day, hour, minute, second))
            }
        }

        return list
    }

    companion object {
        private const val DatabaseVersion = 2
        private const val DatabaseName = "guepardoapps-mynoteencrypted-note.db"
        private const val DatabaseTable = "encryptedNoteTable"

        private const val ColumnId = "_id"
        private const val ColumnTitle = "_title"
        private const val ColumnContent = "_content"
        private const val ColumnYear = "_year"
        private const val ColumnMonth = "_month"
        private const val ColumnDay = "_day"
        private const val ColumnHour = "_hour"
        private const val ColumnMinute = "_minute"
        private const val ColumnSecond = "_second"
    }
}