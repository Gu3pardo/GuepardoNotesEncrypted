package guepardoapps.guepardonotesencrypted.model;

import android.annotation.SuppressLint;
import java.io.Serializable;
import java.util.Calendar;

public class Note implements Serializable {

	private static final long serialVersionUID = -5496323795937961901L;

	private int _id;
	private String _title;
	private String _content;
	private int _day;
	private int _month;
	private int _year;

	public Note(int id, String title, String content, int day, int month, int year) {
		_id = id;
		_title = title;
		_content = content;
		_day = day;
		_month = month;
		_year = year;
	}

	public int GetId() {
		return _id;
	}

	public String GetTitle() {
		return _title;
	}

	public String GetContent() {
		return _content;
	}

	public int GetDay() {
		return _day;
	}

	public int GetMonth() {
		return _month;
	}

	public int GetYear() {
		return _year;
	}

	@SuppressLint("DefaultLocale")
	public String GetDateString() {
		return String.format("%02d.%02d.%04d", _day, _month, _year);
	}

	public void UpdateNote(String title, String content) {
		_title = title;
		_content = content;

		Calendar today = Calendar.getInstance();
		_day = today.get(Calendar.DAY_OF_MONTH);
		_month = today.get(Calendar.MONTH);
		_year = today.get(Calendar.YEAR);
	}

	public void SetTitle(String title) {
		_title = title;
	}

	public void SetContent(String newContent) {
		_content = newContent;
	}
}
