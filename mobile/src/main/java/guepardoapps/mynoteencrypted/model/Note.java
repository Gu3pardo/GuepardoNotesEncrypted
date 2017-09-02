package guepardoapps.mynoteencrypted.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

import android.support.annotation.NonNull;

public class Note implements Serializable {
    private static final long serialVersionUID = -5496323795937961901L;

    private int _id;

    private String _title;
    private String _content;

    private int _day;
    private int _month;
    private int _year;

    private int _hour;
    private int _minute;
    private int _second;

    public Note(
            int id,
            @NonNull String title, @NonNull String content,
            int year, int month, int day,
            int hour, int minute, int second) {
        _id = id;

        _title = title;
        _content = content;

        _day = day;
        _month = month;
        _year = year;

        _hour = hour;
        _minute = minute;
        _second = second;
    }

    public Note(int id, @NonNull String title, @NonNull String content) {
        _id = id;

        _title = title;
        _content = content;

        updateDateTime();
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

    public String GetDateString() {
        return String.format(Locale.getDefault(), "%02d.%02d.%04d", _day, _month + 1, _year);
    }

    public int GetHour() {
        return _hour;
    }

    public int GetMinute() {
        return _minute;
    }

    public int GetSecond() {
        return _second;
    }

    public String GetTimeString() {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", _hour, _minute, _second);
    }

    public void UpdateNote(@NonNull String title, @NonNull String content) {
        _title = title;
        _content = content;
        updateDateTime();
    }

    public void SetTitle(@NonNull String title) {
        _title = title;
        updateDateTime();
    }

    public void SetContent(@NonNull String newContent) {
        _content = newContent;
        updateDateTime();
    }

    private void updateDateTime() {
        Calendar today = Calendar.getInstance();

        _year = today.get(Calendar.YEAR);
        _month = today.get(Calendar.MONTH);
        _day = today.get(Calendar.DAY_OF_MONTH);

        _hour = today.get(Calendar.HOUR_OF_DAY);
        _minute = today.get(Calendar.MINUTE);
        _second = today.get(Calendar.SECOND);
    }
}
