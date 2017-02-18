package guepardoapps.guepardonotesencrypted.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;

import guepardoapps.guepardonotesencrypted.common.DbConstants;
import guepardoapps.guepardonotesencrypted.model.Note;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class Database {

	private DatabaseHelper _databaseHelper;
	private final Context _context;
	private SQLiteDatabase _database;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DbConstants.NAME, null, DbConstants.VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(
					" CREATE TABLE " + DbConstants.TABLE + " ( " 
							+ DbConstants.KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
							+ DbConstants.KEY_TITLE + " TEXT NOT NULL, " 
							+ DbConstants.KEY_NOTES + " TEXT NOT NULL); ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			database.execSQL(" DROP TABLE IF EXISTS " + DbConstants.TABLE);
			onCreate(database);
		}

		public void Remove(Context context) {
			context.deleteDatabase(DbConstants.NAME);
		}
	}

	public Database(Context context) {
		_context = context;
	}

	public Database Open(String passphrase) throws SQLException {
		_databaseHelper = new DatabaseHelper(_context);
		_database = _databaseHelper.getWritableDatabase(passphrase);
		return this;
	}

	public void Close() {
		_databaseHelper.close();
	}

	public long CreateEntry(Note newNote) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(DbConstants.KEY_TITLE, newNote.GetTitle());
		contentValues.put(DbConstants.KEY_NOTES, newNote.GetContent());

		return _database.insert(DbConstants.TABLE, null, contentValues);
	}

	public ArrayList<Note> GetNotes() {
		String[] columns = new String[] { 
				DbConstants.KEY_ROWID, 
				DbConstants.KEY_TITLE, 
				DbConstants.KEY_NOTES };
		Cursor cursor = _database.query(DbConstants.TABLE, columns, null, null, null, null, null);
		ArrayList<Note> result = new ArrayList<Note>();

		int idIndex = cursor.getColumnIndex(DbConstants.KEY_ROWID);
		int titleIndex = cursor.getColumnIndex(DbConstants.KEY_TITLE);
		int noteIndex = cursor.getColumnIndex(DbConstants.KEY_NOTES);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			result.add(new Note(
					cursor.getInt(idIndex), 
					cursor.getString(titleIndex), 
					cursor.getString(noteIndex), 
					0, 0, 0));
		}

		return result;
	}

	public void Update(Note updateNote) throws SQLException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbConstants.KEY_NOTES, updateNote.GetContent());
		_database.update(DbConstants.TABLE, contentValues, DbConstants.KEY_ROWID + "=" + updateNote.GetId(), null);
	}

	public void Delete(Note deleteNote) throws SQLException {
		_database.delete(DbConstants.TABLE, DbConstants.KEY_ROWID + "=" + deleteNote.GetId(), null);
	}

	public void Remove() {
		_databaseHelper.Remove(_context);
	}
}
