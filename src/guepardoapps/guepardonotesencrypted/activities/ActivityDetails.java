package guepardoapps.guepardonotesencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.Toast;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Colors;
import guepardoapps.guepardonotesencrypted.common.Enables;
import guepardoapps.guepardonotesencrypted.common.Bundles;
import guepardoapps.guepardonotesencrypted.controller.*;
import guepardoapps.guepardonotesencrypted.model.Note;

import guepardoapps.library.toastview.ToastView;

import guepardoapps.library.toolset.common.Logger;
import guepardoapps.library.toolset.controller.MailController;
import guepardoapps.library.toolset.controller.NavigationController;
import guepardoapps.library.toolset.controller.NetworkController;

public class ActivityDetails extends Activity {

	private static final String TAG = ActivityDetails.class.getSimpleName();
	private Logger _logger;

	private boolean _noteEdited;
	private Note _note;
	private Note _originalNote;

	private EditText _titleView;
	private EditText _contentView;
	private Button _btnEditSave;
	private Button _btnDelete;
	private ImageButton _btnMail;

	private Context _context;

	private DatabaseController _databaseController;
	private MailController _mailController;
	private NavigationController _navigationController;
	private NetworkController _networkController;
	private NotesDialogController _notesDialogController;

	private Runnable _updateNoteCallback = new Runnable() {
		public void run() {
			_logger.Debug("_updateNoteCallback run");
			_databaseController.UpdateNote(_note);
			resetEditable();
		}
	};

	private Runnable _deleteNoteCallback = new Runnable() {
		public void run() {
			_logger.Debug("_deleteNoteCallback run");
			_databaseController.DeleteNote(_note);
			_navigationController.NavigateTo(ActivityNotes.class, true);
		}
	};

	private Runnable _showOriginalNoteCallback = new Runnable() {
		public void run() {
			_logger.Debug("_showOriginalNoteCallback run");

			_note = _originalNote;
			resetEditable();
			_titleView.setText(_note.GetTitle());
			_contentView.setText(_note.GetContent());
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_details);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_logger = new Logger(TAG, Enables.DEBUGGING);
		_logger.Debug("onCreate");

		_context = this;

		String passphrase = getIntent().getStringExtra(Bundles.PASSPHRASE);
		if (passphrase == null) {
			ToastView.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
			finish();
		}

		_noteEdited = false;

		_databaseController = DatabaseController.getInstance();
		_databaseController.Initialize(_context, passphrase);

		_mailController = new MailController(_context);
		_navigationController = new NavigationController(_context);
		_notesDialogController = new NotesDialogController(_context);
		_networkController = new NetworkController(_context, _notesDialogController);

		Bundle details = getIntent().getExtras();
		_note = new Note(details.getInt(Bundles.NOTE_ID), details.getString(Bundles.NOTE_TITLE),
				details.getString(Bundles.NOTE_CONTENT), 0, 0, 0);
		_originalNote = _note;

		_titleView = (EditText) findViewById(R.id.detailTitle);
		_titleView.setText(_note.GetTitle());
		_titleView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_noteEdited = true;

				_btnEditSave.setVisibility(View.VISIBLE);
				_btnDelete.setVisibility(View.INVISIBLE);

				_note.SetTitle(_titleView.getText().toString());
			}
		});

		_contentView = (EditText) findViewById(R.id.detailContent);
		_contentView.setScroller(new Scroller(_context));
		_contentView.setVerticalScrollBarEnabled(true);
		_contentView.setMovementMethod(new ScrollingMovementMethod());
		_contentView.setText(_note.GetContent());
		_contentView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				_noteEdited = true;

				_btnEditSave.setVisibility(View.VISIBLE);
				_btnDelete.setVisibility(View.INVISIBLE);

				_note.SetContent(_contentView.getText().toString());
			}
		});

		_btnEditSave = (Button) findViewById(R.id.btnEditSave);
		_btnEditSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_btnEditSave onClick");

				if (_noteEdited) {
					_databaseController.UpdateNote(_note);
					resetEditable();
				}
			}
		});

		_btnDelete = (Button) findViewById(R.id.btnDelete);
		_btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_btnDelete onClick");

				if (!_noteEdited) {
					_notesDialogController.ShowDialogDouble("Delete Note?", "Do you want to delete the note?", "Yes",
							_deleteNoteCallback, "No", _notesDialogController.CloseDialogCallback, true);
				}
			}
		});

		_btnMail = (ImageButton) findViewById(R.id.btnMail);
		_btnMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_logger.Debug("_btnMail onClick");

				if (_networkController.IsNetworkAvailable()) {
					_mailController.SendMailWithContent(_note.GetTitle(), _note.GetContent(), false);
				} else {
					ToastView.warning(_context, "Sorry, no network available!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		_logger.Debug("onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		_logger.Debug("onResume");
		_navigationController.NavigateTo(ActivityNotes.class, true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_logger.Debug("onDestroy");
		_databaseController.Dispose();
		_notesDialogController.Dispose();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		_logger.Debug("onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (_noteEdited) {
				_notesDialogController.ShowDialogTriple("Warning!",
						"The created note is not saved! Do you want to save the note?", "Yes", _updateNoteCallback,
						"No", _showOriginalNoteCallback, "Cancel", _notesDialogController.CloseDialogCallback, true);
			} else {
				_navigationController.NavigateTo(ActivityNotes.class, true);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void resetEditable() {
		_noteEdited = false;

		_btnEditSave.setVisibility(View.INVISIBLE);
		_btnDelete.setVisibility(View.VISIBLE);

		_titleView.setFocusable(false);
		_contentView.setFocusable(false);
	}
}