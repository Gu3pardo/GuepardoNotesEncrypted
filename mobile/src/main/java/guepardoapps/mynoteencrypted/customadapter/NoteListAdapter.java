package guepardoapps.mynoteencrypted.customadapter;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rey.material.app.Dialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;

import guepardoapps.mynoteencrypted.R;
import guepardoapps.mynoteencrypted.activities.ActivityDetails;
import guepardoapps.mynoteencrypted.common.constants.*;
import guepardoapps.mynoteencrypted.controller.BroadcastController;
import guepardoapps.mynoteencrypted.controller.DatabaseController;
import guepardoapps.mynoteencrypted.controller.NavigationController;
import guepardoapps.mynoteencrypted.model.Note;
import guepardoapps.mynoteencrypted.tools.Logger;

public class NoteListAdapter extends BaseAdapter {
    private static final String TAG = NoteListAdapter.class.getSimpleName();
    private Logger _logger;

    private Context _context;
    private BroadcastController _broadcastController;
    private NavigationController _navigationController;

    private ArrayList<Note> _notes;

    private static LayoutInflater _inflater = null;

    public NoteListAdapter(@NonNull Context context, @NonNull ArrayList<Note> notes) {
        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("created...");

        _context = context;
        _broadcastController = new BroadcastController(_context);
        _navigationController = new NavigationController(_context);

        _notes = notes;

        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _notes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        TextView _dateTime;
        TextView _title;
        TextView _content;
        FloatingActionButton _btnDelete;
        FloatingActionButton _btnEdit;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = _inflater.inflate(R.layout.list_item, null);

        final Note note = _notes.get(index);

        holder._dateTime = rowView.findViewById(R.id.itemDateTime);
        holder._title = rowView.findViewById(R.id.itemTitle);
        holder._content = rowView.findViewById(R.id.itemContent);

        holder._dateTime.setText(String.format(Locale.getDefault(), "%s / %s", note.GetDateString(), note.GetTimeString()));
        holder._title.setText(note.GetTitle());
        holder._content.setText(note.GetContent());

        holder._btnEdit = rowView.findViewById(R.id.btnEdit);
        holder._btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("_btnEdit onClick");

                Bundle details = new Bundle();
                details.putSerializable(Bundles.NOTE, note);

                _navigationController.NavigateWithData(ActivityDetails.class, details, false);
            }
        });

        holder._btnDelete = rowView.findViewById(R.id.btnDelete);
        holder._btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("_btnDelete onClick");

                boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

                final Dialog dialog = new Dialog(_context);
                dialog
                        .title("Do you want to delete this note?")
                        .positiveAction("Yes")
                        .negativeAction("No")
                        .applyStyle(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog)
                        .setCancelable(true);

                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseController.getInstance().DeleteNote(note);
                        _broadcastController.SendSimpleBroadcast(Broadcasts.NOTE_DELETED);
                        dialog.dismiss();
                    }
                });

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return rowView;
    }
}