package guepardoapps.mynoteencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

import guepardoapps.mynoteencrypted.R;
import guepardoapps.mynoteencrypted.common.constants.*;
import guepardoapps.mynoteencrypted.controller.*;
import guepardoapps.mynoteencrypted.handler.PromptForLoginHandler;
import guepardoapps.mynoteencrypted.model.Note;
import guepardoapps.mynoteencrypted.tools.Logger;

public class ActivityDetails extends Activity {
    private static final String TAG = ActivityDetails.class.getSimpleName();
    private Logger _logger;

    private boolean _noteEdited;
    private Note _note;

    private EditText _titleView;
    private EditText _contentView;
    private TextView _dateTimeView;
    private FloatingActionButton _btnEditSave;
    private FloatingActionButton _btnDelete;

    private Context _context;
    private DatabaseController _databaseController;
    private NavigationController _navigationController;

    private boolean _promptForLogin;
    private PromptForLoginHandler _promptForLoginHandler = PromptForLoginHandler.getInstance();
    private Runnable _promptForLoginRunnable = new Runnable() {
        @Override
        public void run() {
            _logger.Debug("Setting flag to prompt for login!");
            _promptForLogin = true;
        }
    };

    private Runnable _updateNoteCallback = new Runnable() {
        public void run() {
            if (!_databaseController.UpdateNote(_note)) {
                Toasty.error(_context, "Update failed!", Toast.LENGTH_LONG).show();
            } else {
                resetEditable();
            }
        }
    };

    private Runnable _deleteNoteCallback = new Runnable() {
        public void run() {
            if (!_databaseController.DeleteNote(_note)) {
                Toasty.error(_context, "Delete failed!", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_details);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _noteEdited = false;

        _context = this;
        _databaseController = DatabaseController.getInstance();
        _navigationController = new NavigationController(_context);

        _titleView = findViewById(R.id.detailTitle);
        _contentView = findViewById(R.id.detailContent);
        _dateTimeView = findViewById(R.id.detailDateTime);
        _btnEditSave = findViewById(R.id.btnEditSave);
        _btnDelete = findViewById(R.id.btnDelete);

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
                _dateTimeView.setText(String.format(Locale.getDefault(), "%s / %s", _note.GetDateString(), _note.GetTimeString()));
            }
        });

        _contentView.setScroller(new Scroller(_context));
        _contentView.setVerticalScrollBarEnabled(true);
        _contentView.setMovementMethod(new ScrollingMovementMethod());
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
                _dateTimeView.setText(String.format(Locale.getDefault(), "%s / %s", _note.GetDateString(), _note.GetTimeString()));
            }
        });

        Bundle details = getIntent().getExtras();
        _note = (Note) details.getSerializable(Bundles.NOTE);

        if (_note != null) {
            _titleView.setText(_note.GetTitle());
            _contentView.setText(_note.GetContent());
            _dateTimeView.setText(String.format(Locale.getDefault(), "%s / %s", _note.GetDateString(), _note.GetTimeString()));
        } else {
            Toasty.error(_context, "Lost data about note!", Toast.LENGTH_LONG).show();
        }

        _btnEditSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("_btnEditSave onClick");
                if (!_databaseController.UpdateNote(_note)) {
                    Toasty.error(_context, "Update failed!", Toast.LENGTH_LONG).show();
                } else {
                    resetEditable();
                }
            }
        });

        _btnDelete.setOnClickListener(new OnClickListener() {
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
                        _deleteNoteCallback.run();
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

        FloatingActionButton btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("btnShare onClick");

                if (_note != null) {
                    if (_note.GetContent() != null) {
                        Intent sendIntent = new Intent();

                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, _note.GetContent());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        return;
                    }
                }

                Toasty.warning(_context, "Nothing to share!", Toast.LENGTH_LONG).show();
            }
        });

        _promptForLogin = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
        _promptForLoginHandler.postDelayed(_promptForLoginRunnable, 5 * 60 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");

        if (_promptForLogin) {
            Toasty.info(_context, "Due to inactivity you have to login again!", Toast.LENGTH_LONG).show();
            _navigationController.NavigateTo(ActivityLogin.class, true);
        }

        _promptForLoginHandler.removeCallbacks(_promptForLoginRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            _logger.Debug("onKeyDown");

            if (_noteEdited) {
                boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

                final Dialog dialog = new Dialog(_context);
                dialog
                        .title("Warning! The created note is not saved! Do you want to save the note?")
                        .positiveAction("Yes")
                        .negativeAction("No")
                        .applyStyle(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog)
                        .setCancelable(true);

                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _updateNoteCallback.run();
                        dialog.dismiss();
                        finish();
                    }
                });

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        finish();
                    }
                });

                dialog.show();
            } else {
                finish();
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