package guepardoapps.mynoteencrypted.controller

import android.content.Context
import androidx.annotation.NonNull
import guepardoapps.mynoteencrypted.model.Note

internal interface IDatabaseController {
    fun initialize(@NonNull context: Context, @NonNull passphrase: String): Boolean

    fun add(note: Note): Long

    fun update(note: Note): Int

    fun delete(id: String): Int

    fun get(): MutableList<Note>

    fun clearAll()

    fun dispose()
}