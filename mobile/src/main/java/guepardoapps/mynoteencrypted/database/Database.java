package guepardoapps.mynoteencrypted.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import android.support.annotation.NonNull;

import guepardoapps.mynoteencrypted.common.constants.DatabaseConstants;
import guepardoapps.mynoteencrypted.model.Note;

public class Database {
    private static final String KEY_ROW_ID = DatabaseConstants.DATABASE_KEY_ROW_ID;

    private static final String KEY_TITLE = DatabaseConstants.DATABASE_KEY_TITLE;
    private static final String KEY_NOTES = DatabaseConstants.DATABASE_KEY_NOTES;

    private static final String KEY_YEAR = DatabaseConstants.DATABASE_KEY_ROW_YEAR;
    private static final String KEY_MONTH = DatabaseConstants.DATABASE_KEY_ROW_MONTH;
    private static final String KEY_DAY = DatabaseConstants.DATABASE_KEY_ROW_DAY;

    private static final String KEY_HOUR = DatabaseConstants.DATABASE_KEY_ROW_HOUR;
    private static final String KEY_MINUTE = DatabaseConstants.DATABASE_KEY_ROW_MINUTE;
    private static final String KEY_SECOND = DatabaseConstants.DATABASE_KEY_ROW_SECOND;

    private static final String DATABASE_NAME = DatabaseConstants.DATABASE_NAME;
    private static final String DATABASE_TABLE = DatabaseConstants.DATABASE_TABLE;
    private static final int DATABASE_VERSION = DatabaseConstants.DATABASE_VERSION;

    private DatabaseHelper _databaseHelper;
    private final Context _context;
    private SQLiteDatabase _database;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(@NonNull Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(
                    " CREATE TABLE " + DATABASE_TABLE + " ( "
                            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + KEY_TITLE + " TEXT NOT NULL, "
                            + KEY_NOTES + " TEXT NOT NULL, "
                            + KEY_YEAR + " INTEGER, "
                            + KEY_MONTH + " INTEGER, "
                            + KEY_DAY + " INTEGER, "
                            + KEY_HOUR + " INTEGER, "
                            + KEY_MINUTE + " INTEGER, "
                            + KEY_SECOND + " INTEGER); ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL(" DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(database);
        }

        private void Remove(@NonNull Context context) {
            context.deleteDatabase(DatabaseConstants.DATABASE_NAME);
        }
    }

    public Database(@NonNull Context context) {
        _context = context;
    }

    public Database Open(@NonNull String passphrase) throws SQLException {
        _databaseHelper = new DatabaseHelper(_context);
        _database = _databaseHelper.getWritableDatabase(passphrase);
        return this;
    }

    public void Close() {
        _databaseHelper.close();
    }

    public long CreateEntry(@NonNull Note newNote) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TITLE, newNote.GetTitle());
        contentValues.put(KEY_NOTES, newNote.GetContent());

        contentValues.put(KEY_YEAR, newNote.GetYear());
        contentValues.put(KEY_MONTH, newNote.GetMonth());
        contentValues.put(KEY_DAY, newNote.GetDay());

        contentValues.put(KEY_HOUR, newNote.GetHour());
        contentValues.put(KEY_MINUTE, newNote.GetMinute());
        contentValues.put(KEY_SECOND, newNote.GetSecond());

        return _database.insert(DATABASE_TABLE, null, contentValues);
    }

    public ArrayList<Note> GetNotes() {
        String[] columns = new String[]{
                KEY_ROW_ID,
                KEY_TITLE,
                KEY_NOTES,
                KEY_YEAR,
                KEY_MONTH,
                KEY_DAY,
                KEY_HOUR,
                KEY_MINUTE,
                KEY_SECOND};

        Cursor cursor = _database.query(DATABASE_TABLE, columns, null, null, null, null, null);
        ArrayList<Note> result = new ArrayList<>();

        int idIndex = cursor.getColumnIndex(KEY_ROW_ID);

        int titleIndex = cursor.getColumnIndex(KEY_TITLE);
        int noteIndex = cursor.getColumnIndex(KEY_NOTES);

        int yearIndex = cursor.getColumnIndex(KEY_YEAR);
        int monthIndex = cursor.getColumnIndex(KEY_MONTH);
        int dayIndex = cursor.getColumnIndex(KEY_DAY);

        int hourIndex = cursor.getColumnIndex(KEY_HOUR);
        int minuteIndex = cursor.getColumnIndex(KEY_MINUTE);
        int secondIndex = cursor.getColumnIndex(KEY_SECOND);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result.add(new Note(
                    cursor.getInt(idIndex),
                    cursor.getString(titleIndex), cursor.getString(noteIndex),
                    cursor.getInt(yearIndex), cursor.getInt(monthIndex), cursor.getInt(dayIndex),
                    cursor.getInt(hourIndex), cursor.getInt(minuteIndex), cursor.getInt(secondIndex)));
        }

        cursor.close();

        return result;
    }

    public boolean Update(@NonNull Note updateNote) throws SQLException {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TITLE, updateNote.GetTitle());
        contentValues.put(KEY_NOTES, updateNote.GetContent());

        contentValues.put(KEY_YEAR, updateNote.GetYear());
        contentValues.put(KEY_MONTH, updateNote.GetMonth());
        contentValues.put(KEY_DAY, updateNote.GetDay());

        contentValues.put(KEY_HOUR, updateNote.GetHour());
        contentValues.put(KEY_MINUTE, updateNote.GetMinute());
        contentValues.put(KEY_SECOND, updateNote.GetSecond());

        _database.update(DATABASE_TABLE, contentValues, KEY_ROW_ID + "=" + updateNote.GetId(), null);

        return true;
    }

    public boolean Delete(@NonNull Note deleteNote) throws SQLException {
        _database.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + deleteNote.GetId(), null);
        return true;
    }

    public void Remove() {
        _databaseHelper.Remove(_context);
    }
}
