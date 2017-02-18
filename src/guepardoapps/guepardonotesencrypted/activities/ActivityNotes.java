package guepardoapps.guepardonotesencrypted.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Bundles;
import guepardoapps.guepardonotesencrypted.common.Colors;
import guepardoapps.guepardonotesencrypted.controller.DatabaseController;
import guepardoapps.guepardonotesencrypted.customadapter.NoteListAdapter;
import guepardoapps.guepardonotesencrypted.model.Note;

import guepardoapps.toolset.services.NavigationService;

public class ActivityNotes extends Activity {

	private ArrayList<Note> _noteList;

	private boolean _created;
	private String _passphrase;

	private ListView _listView;
	private ProgressBar _progressBar;
	private Button _btnAdd, _btnImpressum;

	private Context _context;

	private DatabaseController _databaseController;
	private NavigationService _navigationService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_main);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_context = this;

		String passphrase = getIntent().getStringExtra(Bundles.PASSPHRASE);
		if (passphrase == null) {
			Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
			finish();
		}
		_passphrase = passphrase;

		_databaseController = new DatabaseController(_context);
		_navigationService = new NavigationService(_context);

		_listView = (ListView) findViewById(R.id.listView);
		_progressBar = (ProgressBar) findViewById(R.id.progressBar);

		_btnAdd = (Button) findViewById(R.id.goToAddView);
		_btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_navigationService.NavigateTo(ActivityAdd.class, true);
			}
		});

		_btnImpressum = (Button) findViewById(R.id.btnImpressum);
		_btnImpressum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_navigationService.NavigateTo(ActivityImpressum.class, true);
			}
		});

		_created = true;
	}

	protected void onResume() {
		super.onResume();
		if (_created) {
			if (_passphrase == null) {
				Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
				finish();
			}

			_noteList = _databaseController.GetNotes(_passphrase);

			_listView.setAdapter(new NoteListAdapter(_context, _noteList));

			_progressBar.setVisibility(View.GONE);
			_listView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}