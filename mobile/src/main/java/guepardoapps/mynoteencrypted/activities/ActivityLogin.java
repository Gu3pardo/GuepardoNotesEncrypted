package guepardoapps.mynoteencrypted.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Button;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

import guepardoapps.mynoteencrypted.R;
import guepardoapps.mynoteencrypted.common.constants.Bundles;
import guepardoapps.mynoteencrypted.common.constants.SharedPrefConstants;
import guepardoapps.mynoteencrypted.controller.DatabaseController;
import guepardoapps.mynoteencrypted.controller.NavigationController;
import guepardoapps.mynoteencrypted.controller.SharedPrefController;
import guepardoapps.mynoteencrypted.model.Note;
import guepardoapps.mynoteencrypted.tools.Logger;
import guepardoapps.mynoteencrypted.tools.PasswordStrengthHelper;
import guepardoapps.mynoteencrypted.tools.Tools;

public class ActivityLogin extends Activity {
    private static final String TAG = ActivityLogin.class.getSimpleName();
    private Logger _logger;

    private Context _context;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;
    private static final int MAX_INVALID_INPUT = 5;
    private int _invalidInputCount = 0;

    private DatabaseController _databaseController;
    private NavigationController _navigationController;
    private SharedPrefController _sharedPrefController;

    private EditText _passphraseInput;

    private boolean _isPasswordLongEnough;
    private boolean _isPasswordTooLong;
    private boolean _isPasswordValid;
    private boolean _doPasswordsMatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.side_login);

        _logger = new Logger(TAG);
        _logger.Debug("onCreate");

        _context = this;

        _databaseController = DatabaseController.getInstance();
        _navigationController = new NavigationController(_context);
        _sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

        initializeViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        _logger.Debug("onResume");

        if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.ENTERED_APPLICATION_PASSWORD)) {
            showDialogFirstLogin();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        _logger.Debug("onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
    }


    private void initializeViews() {
        _logger.Debug("initializeViews");

        _passphraseInput = ((Activity) _context).findViewById(R.id.passwordInput);

        Bitmap bitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.main_image_informations);
        bitmap = Tools.GetCircleBitmap(bitmap);
        ImageView loginImageView = ((Activity) _context).findViewById(R.id.loginImageView);
        loginImageView.setImageBitmap(bitmap);

        Button loginButton = ((Activity) _context).findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passphrase = _passphraseInput.getText().toString();

                if (passphrase.length() >= MIN_PASSWORD_LENGTH && passphrase.length() <= MAX_PASSWORD_LENGTH) {
                    if (_databaseController.Initialize(_context, passphrase)) {
                        Bundle data = new Bundle();
                        data.putString(Bundles.PASSPHRASE, passphrase);
                        _navigationController.NavigateWithData(ActivityNotes.class, data, true);
                    } else {
                        loginError();
                    }
                } else {
                    loginError();
                }
            }
        });
    }

    private void loginError() {
        _logger.Error("Password is not valid!");
        Toasty.error(_context, "Password is not valid!", Toast.LENGTH_SHORT).show();
        increaseInvalidLoginCount();
    }

    private void increaseInvalidLoginCount() {
        _logger.Warn("increaseInvalidLoginCount");
        _invalidInputCount++;

        if (_invalidInputCount < MAX_INVALID_INPUT) {
            Toasty.warning(_context,
                    String.format(
                            Locale.getDefault(),
                            "Caution! You only have %d more tries! Then your data will be erased!",
                            MAX_INVALID_INPUT - _invalidInputCount),
                    Toast.LENGTH_LONG).show();
        }

        if (_invalidInputCount >= MAX_INVALID_INPUT) {
            _logger.Warn("Entered too many times an invalid input!");

            DatabaseController databaseController = DatabaseController.getInstance();
            databaseController.ClearAll();
            databaseController.Dispose();

            _sharedPrefController.RemoveSharedPreferences();

            Toasty.error(_context,
                    String.format(
                            Locale.getDefault(),
                            "You entered %d times an invalid login! Your data was erased!",
                            MAX_INVALID_INPUT),
                    Toast.LENGTH_LONG).show();

            ((Activity) _context).finish();
        }
    }

    private void showDialogFirstLogin() {
        _logger.Debug("showDialogFirstLogin");

        final Dialog dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_first_login);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        } else {
            _logger.Error("Windows is null!");
        }

        final TextView passwordStrengthTextView = dialog.findViewById(R.id.passwordStrengthTextView);
        final TextView passwordCheckTextView = dialog.findViewById(R.id.passwordCheckTextView);

        final EditText passwordInput = dialog.findViewById(R.id.passwordInput);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                _isPasswordLongEnough = editable.toString().length() >= MIN_PASSWORD_LENGTH;
                _isPasswordTooLong = editable.toString().length() > MAX_PASSWORD_LENGTH;

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
                    passwordStrengthTextView.setText(_context.getString(R.string.tooShort));
                } else if (_isPasswordTooLong) {
                    passwordStrengthTextView.setTextColor(Color.RED);
                    passwordStrengthTextView.setText(_context.getString(R.string.tooLong));
                } else {
                    if (!_isPasswordValid) {
                        passwordStrengthTextView.setTextColor(Color.RED);
                        passwordStrengthTextView.setText(_context.getString(R.string.notValid));
                    } else {
                        passwordStrengthTextView.setTextColor(Color.GREEN);
                        passwordStrengthTextView.setText(_context.getString(R.string.valid));
                    }
                }
            }
        });

        final EditText passwordReenterInput = dialog.findViewById(R.id.passwordReenterInput);
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
                        passwordCheckTextView.setText(_context.getString(R.string.match));
                    } else {
                        _doPasswordsMatch = false;
                        passwordCheckTextView.setTextColor(Color.RED);
                        passwordCheckTextView.setText(_context.getString(R.string.noMatch));
                    }
                } else {
                    _doPasswordsMatch = false;
                    passwordCheckTextView.setTextColor(Color.RED);
                    passwordCheckTextView.setText(_context.getString(R.string.wrongLength));
                }
            }
        });

        Button buttonSaveApplicationPassword = dialog.findViewById(R.id.buttonSaveApplicationPassword);
        buttonSaveApplicationPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_isPasswordLongEnough) {
                    Toasty.error(_context, "Password is too short!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!_isPasswordValid) {
                    Toasty.error(_context, "Password not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!_doPasswordsMatch) {
                    Toasty.error(_context, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = passwordInput.getText().toString();

                if (_databaseController.Initialize(_context, password)) {
                    _sharedPrefController.SaveBooleanValue(SharedPrefConstants.ENTERED_APPLICATION_PASSWORD, true);

                    _databaseController.SaveNote(new Note(0, "Title", getResources().getString(R.string.example)));
                    _databaseController.Dispose();

                    Toasty.success(_context, "Saved!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    Toasty.error(_context, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }
}