package guepardoapps.mynoteencrypted.controller

import android.annotation.SuppressLint
import android.content.Context
import guepardoapps.mynoteencrypted.database.note.DbNote
import guepardoapps.mynoteencrypted.model.Note
import net.sqlcipher.SQLException
import net.sqlcipher.database.SQLiteDatabase

internal class DatabaseController private constructor() : IDatabaseController {

    private var dbNote: DbNote? = null

    private var initialized: Boolean = false

    private object Holder {
        @SuppressLint("StaticFieldLeak")
        val instance: DatabaseController = DatabaseController()
    }

    companion object {
        val instance: DatabaseController by lazy { Holder.instance }
    }

    override fun initialize(context: Context, passphrase: String): Boolean {
        if (initialized) {
            return false
        }

        SQLiteDatabase.loadLibs(context)

        return try {
            dbNote = DbNote(context, passphrase)
            initialized = true
            true
        } catch (sqlException: SQLException) {
            false
        }
    }

    override fun add(note: Note): Long = if (!initialized) { -1 } else { dbNote!!.add(note) }

    override fun update(note: Note): Int = if (!initialized) { -1 } else {  dbNote!!.update(note) }

    override fun delete(id: String): Int = if (!initialized) { -1 } else { dbNote!!.delete(id) }

    override fun get(): MutableList<Note> = if (!initialized) { mutableListOf() } else { dbNote!!.get() }

    override fun clearAll() {
        if (initialized) {
            dbNote!!.get().forEach { x -> dbNote!!.delete(x.id) }
        }
    }

    override fun dispose() {
        dbNote = null
        initialized = false
    }
}