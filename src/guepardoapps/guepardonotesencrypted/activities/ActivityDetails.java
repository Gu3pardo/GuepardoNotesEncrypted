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

import es.dmoral.toasty.Toasty;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Colors;
import guepardoapps.guepardonotesencrypted.common.Bundles;
import guepardoapps.guepardonotesencrypted.controller.*;
import guepardoapps.guepardonotesencrypted.model.Note;

import guepardoapps.toolset.controller.DialogController;
import guepardoapps.toolset.controller.NetworkController;
import guepardoapps.toolset.services.MailService;
import guepardoapps.toolset.services.NavigationService;

public class ActivityDetails extends Activity {

	private boolean _noteEdited;
	private Note _note;
	private Note _originalNote;
	private String _passphrase;

	private EditText _titleView;
	private EditText _contentView;
	private Button _btnEditSave;
	private Button _btnDelete;
	private ImageButton _btnMail;

	private Context _context;

	private DatabaseController _databaseController;
	private DialogController _dialogController;
	private MailService _mailService;
	private NavigationService _navigationService;
	private NetworkController _networkController;

	private Runnable updateNoteCallback = new Runnable() {
		public void run() {
			if (_passphrase == null) {
				Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
				finish();
			}

			_databaseController.UpdateNote(_passphrase, _note);
			resetEditable();
		}
	};

	private Runnable deleteNoteCallback = new Runnable() {
		public void run() {
			if (_passphrase == null) {
				Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
				finish();
			}

			_databaseController.DeleteNote(_passphrase, _note);
			_navigationService.NavigateTo(ActivityNotes.class, true);
		}
	};

	private Runnable showOriginalNoteCallback = new Runnable() {
		public void run() {
			_note = _originalNote;
			resetEditable();
			_titleView.setText(_note.GetTitle());
			_contentView.setText(_note.GetContent());
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_details);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_context = this;

		String passphrase = getIntent().getStringExtra(Bundles.PASSPHRASE);
		if (passphrase == null) {
			Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
			finish();
		}
		_passphrase = passphrase;

		_noteEdited = false;

		_databaseController = new DatabaseController(_context);
		_dialogController = new DialogController(_context, getResources().getColor(R.color.TextIcon),
				getResources().getColor(R.color.Primary));
		_mailService = new MailService(_context);
		_navigationService = new NavigationService(_context);
		_networkController = new NetworkController(_context, _dialogController);

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
				if (_noteEdited) {
					if (_passphrase == null) {
						Toasty.error(_context, "Failed to read passphrase!", Toast.LENGTH_LONG).show();
						finish();
					}

					_databaseController.UpdateNote(_passphrase, _note);

					resetEditable();
				}
			}
		});

		_btnDelete = (Button) findViewById(R.id.btnDelete);
		_btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!_noteEdited) {
					_dialogController.ShowDialogDouble("Delete Note?", "Do you want to delete the note?", "Yes",
							deleteNoteCallback, "No", _dialogController.CloseDialogCallback, true);
				}
			}
		});

		_btnMail = (ImageButton) findViewById(R.id.btnMail);
		_btnMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_networkController.IsNetworkAvailable()) {
					_mailService.SendMailWithContent(_note.GetTitle(), _note.GetContent(), false);
				} else {
					Toasty.warning(_context, "Sorry, no network available!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (_noteEdited) {
				_dialogController.ShowDialogTriple("Warning!",
						"The created note is not saved! Do you want to save the note?", "Yes", updateNoteCallback, "No",
						showOriginalNoteCallback, "Cancel", _dialogController.CloseDialogCallback, true);
			} else {
				_navigationService.NavigateTo(ActivityNotes.class, true);
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