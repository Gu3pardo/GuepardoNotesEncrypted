package guepardoapps.mynoteencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.ThemeManager;
import com.rey.material.widget.FloatingActionButton;

import es.dmoral.toasty.Toasty;

import guepardoapps.mynoteencrypted.R;
import guepardoapps.mynoteencrypted.common.constants.*;
import guepardoapps.mynoteencrypted.controller.DatabaseController;
import guepardoapps.mynoteencrypted.controller.NavigationController;
import guepardoapps.mynoteencrypted.handler.PromptForLoginHandler;
import guepardoapps.mynoteencrypted.model.Note;
import guepardoapps.mynoteencrypted.tools.Logger;

public class ActivityAdd extends Activity {
    private static final String TAG = ActivityAdd.class.getSimpleName();
    private Logger _logger;

    private String _title;
    private String _content;

    private EditText _editTitle;
    private EditText _editContent;

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

    private Runnable _trySaveNewNoteCallback = new Runnable() {
        public void run() {
            _logger.Debug("_trySaveNewNoteCallback run");

            if (_title == null) {
                Toasty.error(_context, "Please enter a title!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (_title.isEmpty()) {
                Toasty.error(_context, "Please enter a title!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (_content == null) {
                Toasty.error(_context, "Please enter a note!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (_content.isEmpty()) {
                Toasty.error(_context, "Please enter a note!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!_databaseController.SaveNote(new Note(-1, _title, _content))) {
                Toasty.error(_context, "Save failed!", Toast.LENGTH_LONG).show();

                boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;

                final Dialog dialog = new Dialog(_context);
                dialog
                        .title("Warning! Save failed! Try again or cancel?")
                        .positiveAction("Retry")
                        .negativeAction("Cancel")
                        .applyStyle(isLightTheme ? R.style.SimpleDialogLight : R.style.SimpleDialog)
                        .setCancelable(true);

                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _trySaveNewNoteCallback.run();
                        dialog.dismiss();
                    }
                });

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _finishCallback.run();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            } else {
                _finishCallback.run();
            }
        }
    };

    private Runnable _finishCallback = new Runnable() {
        public void run() {
            _logger.Debug("_finishCallback run");
            finish();
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_add);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _context = this;
        _databaseController = DatabaseController.getInstance();
        _navigationController = new NavigationController(_context);

        _editTitle = findViewById(R.id.addTitle);
        _editTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _title = _editTitle.getText().toString();
            }
        });

        _editContent = findViewById(R.id.addContent);
        _editContent.setScroller(new Scroller(_context));
        _editContent.setMovementMethod(new ScrollingMovementMethod());
        _editContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _content = _editContent.getText().toString();
            }
        });

        FloatingActionButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("_btnSave onClick");
                _trySaveNewNoteCallback.run();
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
        _logger.Debug("onKeyDown");

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (_title != null || _content != null) {
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
                        _trySaveNewNoteCallback.run();
                        dialog.dismiss();
                    }
                });

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _finishCallback.run();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            } else {
                _finishCallback.run();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}