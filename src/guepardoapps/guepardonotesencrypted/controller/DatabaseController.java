package guepardoapps.guepardonotesencrypted.controller;

import java.util.ArrayList;

import android.content.Context;

import guepardoapps.guepardonotesencrypted.database.Database;
import guepardoapps.guepardonotesencrypted.model.Note;

import net.sqlcipher.database.SQLiteDatabase;

public class DatabaseController {

	private Context _context;
	private static Database _database;

	public DatabaseController(Context context) {
		_context = context;
		SQLiteDatabase.loadLibs(_context);
		_database = new Database(_context);
	}

	public ArrayList<Note> GetNotes(String passphrase) {
		try {
			_database.Open(passphrase);
		} catch (Exception ex) {
			return null;
		}

		ArrayList<Note> notes = _database.GetNotes();
		_database.Close();

		return notes;
	}

	public void SaveNote(String passphrase, Note newNote) {
		_database.Open(passphrase);
		_database.CreateEntry(newNote);
		_database.Close();
	}

	public void UpdateNote(String passphrase, Note updateNote) {
		_database.Open(passphrase);
		_database.Update(updateNote);
		_database.Close();
	}

	public void DeleteNote(String passphrase, Note deleteNote) {
		_database.Open(passphrase);
		_database.Delete(deleteNote);
		_database.Close();
	}

	public void ClearNotes(String passphrase) {
		_database.Open(passphrase);
		_database.Close();
		_database.Remove();
	}
}
