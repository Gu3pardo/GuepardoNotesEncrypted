package guepardoapps.guepardonotesencrypted.customadapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.activities.ActivityDetails;
import guepardoapps.guepardonotesencrypted.common.Bundles;
import guepardoapps.guepardonotesencrypted.common.Enables;
import guepardoapps.guepardonotesencrypted.model.Note;

import guepardoapps.library.toolset.common.Logger;
import guepardoapps.library.toolset.controller.NavigationController;

public class NoteListAdapter extends BaseAdapter {

	private static final String TAG = NoteListAdapter.class.getSimpleName();
	private Logger _logger;

	private Context _context;
	private NavigationController _navigationController;

	private ArrayList<Note> _notes;

	private static LayoutInflater _inflater = null;

	public NoteListAdapter(Context context, ArrayList<Note> notes) {
		_logger = new Logger(TAG, Enables.DEBUGGING);
		_logger.Debug("created...");

		_context = context;
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

	public class Holder {
		TextView _title;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int index, View convertView, ViewGroup parent) {
		Holder holder = new Holder();
		View rowView = _inflater.inflate(R.layout.list_item, null);

		holder._title = (TextView) rowView.findViewById(R.id.itemTitle);
		holder._title.setText(_notes.get(index).GetTitle());
		holder._title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_title onClick");

				Bundle details = new Bundle();
				details.putInt(Bundles.NOTE_ID, _notes.get(index).GetId());
				details.putString(Bundles.NOTE_TITLE, _notes.get(index).GetTitle());
				details.putString(Bundles.NOTE_CONTENT, _notes.get(index).GetContent());

				_navigationController.NavigateWithData(ActivityDetails.class, details, true);
			}
		});

		return rowView;
	}
}