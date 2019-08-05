package guepardoapps.mynoteencrypted.activities

import android.app.Activity
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.guepardoapps.kulid.ULID
import com.rey.material.widget.Button
import es.dmoral.toasty.Toasty
import guepardoapps.mynoteencrypted.R
import guepardoapps.mynoteencrypted.controller.DatabaseController
import guepardoapps.mynoteencrypted.controller.NavigationController
import guepardoapps.mynoteencrypted.controller.SharedPreferenceController
import guepardoapps.mynoteencrypted.extensions.circleBitmap
import guepardoapps.mynoteencrypted.model.Note
import guepardoapps.mynoteencrypted.utils.checkValidity
import java.util.Locale

@ExperimentalUnsignedTypes
class ActivityBoot : Activity() {

    private val databaseController: DatabaseController = DatabaseController.instance

    private lateinit var sharedPreferenceController: SharedPreferenceController

    private var doPasswordsMatch: Boolean = false

    private var invalidInputCount: Int = 0

    private var isPasswordValid: Boolean = false

    private lateinit var passphraseInput: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.side_login)

        sharedPreferenceController = SharedPreferenceController(this)
        invalidInputCount = sharedPreferenceController.load(getString(R.string.sharedPrefInvalidEnteredCount), 0)

        passphraseInput = findViewById(R.id.passwordInput)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.about)
        findViewById<ImageView>(R.id.loginImageView).setImageBitmap(bitmap.circleBitmap(bitmap.height, bitmap.width, Bitmap.Config.ARGB_8888.ordinal))

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            if (passphraseInput.text.toString().length in MinPasswordLength..MaxPasswordLength) {
                if (databaseController.initialize(applicationContext, passphraseInput.text.toString())) {
                    NavigationController(this).navigate(ActivityMain::class.java, true)
                } else {
                    loginError()
                }
            } else {
                loginError()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (!sharedPreferenceController.load(getString(R.string.sharedPrefEnteredPassword), false)) {
            showDialogFirstLogin()
        }
    }

    private fun loginError() {
        invalidInputCount++

        Toasty.error(this, getString(R.string.passwordNotValid), Toast.LENGTH_SHORT).show()

        if (invalidInputCount < MaxInvalidInput) {
            Toasty.warning(this,
                    String.format(
                            Locale.getDefault(),
                            getString(R.string.invalidPwDbDeleteWarning),
                            MaxInvalidInput - invalidInputCount),
                    Toast.LENGTH_LONG).show()
        } else {
            databaseController.clearAll()
            databaseController.dispose()
            Toasty.error(this,
                    String.format(
                            Locale.getDefault(),
                            getString(R.string.invalidPwDbDeleteHint),
                            MaxInvalidInput),
                    Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showDialogFirstLogin() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_first_login)

        val layoutParams = WindowManager.LayoutParams()
        val window = dialog.window
        if (window != null) {
            layoutParams.copyFrom(window.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = layoutParams
        }

        val passwordStrengthTextView = dialog.findViewById<TextView>(R.id.passwordStrengthTextView)
        val passwordCheckTextView = dialog.findViewById<TextView>(R.id.passwordCheckTextView)

        val passwordInput = dialog.findViewById<EditText>(R.id.passwordInput)
        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                isPasswordValid = false
                if (editable.toString().length < MinPasswordLength) {
                    passwordStrengthTextView.setTextColor(Color.RED)
                    passwordStrengthTextView.text = getString(R.string.tooShort)
                } else if (editable.toString().length > MaxPasswordLength) {
                    passwordStrengthTextView.setTextColor(Color.RED)
                    passwordStrengthTextView.text = getString(R.string.tooLong)
                } else {
                    if (!checkValidity(editable.toString())) {
                        passwordStrengthTextView.setTextColor(Color.RED)
                        passwordStrengthTextView.text = getString(R.string.notValid)
                    } else {
                        isPasswordValid = true
                        passwordStrengthTextView.setTextColor(Color.GREEN)
                        passwordStrengthTextView.text = getString(R.string.valid)
                    }
                }
            }

            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}
        })

        dialog.findViewById<EditText>(R.id.passwordReenterInput).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                checkMatch(editable.toString())
            }

            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

            private fun checkMatch(reenteredPassword: String) {
                if (passwordInput.text.toString().length == reenteredPassword.length) {
                    if (passwordInput.text.toString().contains(reenteredPassword)) {
                        doPasswordsMatch = true
                        passwordCheckTextView.setTextColor(Color.GREEN)
                        passwordCheckTextView.text = getString(R.string.match)
                    } else {
                        doPasswordsMatch = false
                        passwordCheckTextView.setTextColor(Color.RED)
                        passwordCheckTextView.text = getString(R.string.noMatch)
                    }
                } else {
                    doPasswordsMatch = false
                    passwordCheckTextView.setTextColor(Color.RED)
                    passwordCheckTextView.text = getString(R.string.wrongLength)
                }
            }
        })

        dialog.findViewById<Button>(R.id.buttonSaveApplicationPassword).setOnClickListener {
            if (!isPasswordValid) {
                Toasty.error(this, getString(R.string.passwordNotValid), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!doPasswordsMatch) {
                Toasty.error(this, getString(R.string.passwordsNotMatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (databaseController.initialize(this, passwordInput.text.toString())) {
                sharedPreferenceController.save(getString(R.string.sharedPrefEnteredPassword), true)
                databaseController.add(Note(id = ULID.random(), title = getString(R.string.title), content = resources.getString(R.string.example)))
                Toasty.success(this, getString(R.string.saved), Toast.LENGTH_LONG).show()
                dialog.dismiss()
                NavigationController(this).navigate(ActivityMain::class.java, true)
            } else {
                Toasty.error(this, getString(R.string.unknownError), Toast.LENGTH_LONG).show()
            }
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    companion object {
        private const val MinPasswordLength = 8
        private const val MaxPasswordLength = 64
        private const val MaxInvalidInput = 5
    }
}