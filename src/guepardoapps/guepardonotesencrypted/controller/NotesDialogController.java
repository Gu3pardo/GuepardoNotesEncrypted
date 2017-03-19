package guepardoapps.guepardonotesencrypted.controller;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.SharedPrefConstants;
import guepardoapps.guepardonotesencrypted.helper.PasswordStrengthHelper;
import guepardoapps.guepardonotesencrypted.model.Note;

import guepardoapps.library.toastview.ToastView;

import guepardoapps.toolset.controller.DialogController;
import guepardoapps.toolset.controller.SharedPrefController;

public class NotesDialogController extends DialogController {

	private boolean _isPasswordLongEnough = false;
	private boolean _isPasswordValid = false;
	private boolean _doPasswordsMatch = false;

	private DatabaseController _databaseController;
	private SharedPrefController _sharedPrefController;

	public NotesDialogController(Context context) {
		super(context, ContextCompat.getColor(context, R.color.TextIcon),
				ContextCompat.getColor(context, R.color.Primary));
		_context = context;
		_databaseController = new DatabaseController(_context);
		_sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);
	}

	public void ShowDialogFirstLogin(final Runnable runnable) {
		_logger.Debug("ShowDialogFirstLogin");

		createNewDialog(Window.FEATURE_NO_TITLE, R.layout.dialog_first_login);

		final TextView passwordStrengthTextView = (TextView) _dialog.findViewById(R.id.passwordStrengthTextView);
		final TextView passwordCheckTextView = (TextView) _dialog.findViewById(R.id.passwordCheckTextView);

		final EditText passwordInput = (EditText) _dialog.findViewById(R.id.passwordInput);
		passwordInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.toString().length() >= PasswordStrengthHelper.MIN_PASSWORD_LENGTH) {
					_isPasswordLongEnough = true;
				} else {
					_isPasswordLongEnough = false;
				}

				_isPasswordValid = PasswordStrengthHelper.CheckValidity(editable.toString());
				setPasswordStrengthTextView();
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			}

			private void setPasswordStrengthTextView() {
				if (!_isPasswordLongEnough) {
					passwordStrengthTextView.setTextColor(Color.RED);
					passwordStrengthTextView.setText("Too short!");
				} else {
					if (!_isPasswordValid) {
						passwordStrengthTextView.setTextColor(Color.RED);
						passwordStrengthTextView.setText("Not valid!");
					} else {
						passwordStrengthTextView.setTextColor(Color.GREEN);
						passwordStrengthTextView.setText("Valid!");
					}
				}
			}
		});
		final EditText passwordReenterInput = (EditText) _dialog.findViewById(R.id.passwordReenterInput);
		passwordReenterInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {
				checkMatch(editable.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			}

			private void checkMatch(String reenteredPassword) {
				if (passwordInput.getText().toString().length() == reenteredPassword.length()) {
					if (passwordInput.getText().toString().contains(reenteredPassword)) {
						_doPasswordsMatch = true;
						passwordCheckTextView.setTextColor(Color.GREEN);
						passwordCheckTextView.setText("Match!");
					} else {
						_doPasswordsMatch = false;
						passwordCheckTextView.setTextColor(Color.RED);
						passwordCheckTextView.setText("No match!");
					}
				} else {
					_doPasswordsMatch = false;
					passwordCheckTextView.setTextColor(Color.RED);
					passwordCheckTextView.setText("Wrong length!");
				}
			}
		});

		Button buttonSaveApplicationPassword = (Button) _dialog.findViewById(R.id.buttonSaveApplicationPassword);
		buttonSaveApplicationPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!_isPasswordLongEnough) {
					ToastView.error(_context, "Password is too short!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!_isPasswordValid) {
					ToastView.error(_context, "Password not valid!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!_doPasswordsMatch) {
					ToastView.error(_context, "Passwords do not match!", Toast.LENGTH_SHORT).show();
					return;
				}

				String password = passwordInput.getText().toString();

				_databaseController.SaveNote(password, new Note(0, "Title",
						String.format(_context.getResources().getString(R.string.example)), 0, 0, 0));
				_sharedPrefController.SaveBooleanValue(SharedPrefConstants.SHARED_PREF_NAME, true);

				if (runnable != null) {
					runnable.run();
				}

				CloseDialogCallback.run();
			}
		});

		displayNewDialog(false);
	}

	public void ShowDialogLogin(final Runnable runnable) {
		_logger.Debug("ShowDialogLogin");

		createNewDialog(Window.FEATURE_NO_TITLE, R.layout.dialog_login);

		final EditText passwordInput = (EditText) _dialog.findViewById(R.id.passwordInput);

		Button buttonSaveApplicationPassword = (Button) _dialog.findViewById(R.id.buttonSaveApplicationPassword);
		buttonSaveApplicationPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String password = passwordInput.getText().toString();
				if (password == null) {
					ToastView.error(_context, "Login failed!", Toast.LENGTH_LONG).show();
					return;
				}

				if (_databaseController.GetNotes(password) == null) {
					ToastView.error(_context, "Login failed!", Toast.LENGTH_LONG).show();
					return;
				}

				if (runnable != null) {
					runnable.run();
				}

				CloseDialogCallback.run();
			}
		});

		displayNewDialog(false);
	}

	private void createNewDialog(int windowFeature, int layoutId) {
		_logger.Debug("createNewDialog");

		if (_dialog != null) {
			_logger.Warn("Dialog open! Closing dialog...");
			CloseDialogCallback.run();
		}

		_dialog = new Dialog(_context);
		_dialog.requestWindowFeature(windowFeature);
		_dialog.setContentView(layoutId);

		setDefaultLayoutParams();
	}

	private void setDefaultLayoutParams() {
		_logger.Debug("setDefaultLayoutParams");

		if (_dialog == null) {
			_logger.Error("_dialog is null!");
			return;
		}

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		Window window = _dialog.getWindow();
		layoutParams.copyFrom(window.getAttributes());
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(layoutParams);
	}

	private void displayNewDialog(boolean cancelable) {
		_logger.Debug("displayNewDialog");

		if (_dialog == null) {
			_logger.Error("_dialog is null!");
			return;
		}

		_dialog.setCancelable(cancelable);
		_isDialogOpen = true;
		_dialog.show();
	}
}
