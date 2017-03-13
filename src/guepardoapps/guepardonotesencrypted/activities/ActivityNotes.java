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
import guepardoapps.guepardonotesencrypted.common.Enables;
import guepardoapps.guepardonotesencrypted.controller.DatabaseController;
import guepardoapps.guepardonotesencrypted.customadapter.NoteListAdapter;
import guepardoapps.guepardonotesencrypted.model.Note;

import guepardoapps.toolset.common.Logger;
import guepardoapps.toolset.controller.NavigationController;

public class ActivityNotes extends Activity {

	private static final String TAG = ActivityNotes.class.getSimpleName();
	private Logger _logger;

	private ArrayList<Note> _noteList;

	private boolean _created;
	private String _passphrase;

	private ListView _listView;
	private ProgressBar _progressBar;
	private Button _btnAdd, _btnImpressum;

	private Context _context;

	private DatabaseController _databaseController;
	private NavigationController _navigationController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_main);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_logger = new Logger(TAG, Enables.DEBUGGING);
		_logger.Debug("onCreate");

		_context = this;

		String passphrase = getIntent().getStringExtra(Bundles.PASSPHRASE);
		if (passphrase == null) {
			Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
			finish();
		}
		_passphrase = passphrase;

		_databaseController = new DatabaseController(_context);
		_navigationController = new NavigationController(_context);

		_listView = (ListView) findViewById(R.id.listView);
		_progressBar = (ProgressBar) findViewById(R.id.progressBar);

		_btnAdd = (Button) findViewById(R.id.goToAddView);
		_btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_btnAdd onClick");

				_navigationController.NavigateTo(ActivityAdd.class, true);
			}
		});

		_btnImpressum = (Button) findViewById(R.id.btnImpressum);
		_btnImpressum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_btnImpressum onClick");

				_navigationController.NavigateTo(ActivityImpressum.class, true);
			}
		});

		_created = true;
	}

	protected void onResume() {
		super.onResume();
		_logger.Debug("onResume");

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
}