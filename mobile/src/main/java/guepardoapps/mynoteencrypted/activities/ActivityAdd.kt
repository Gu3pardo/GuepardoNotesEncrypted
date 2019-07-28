package guepardoapps.mynoteencrypted.activities

import android.app.Activity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Scroller
import android.widget.Toast
import com.github.guepardoapps.kulid.ULID
import com.rey.material.widget.FloatingActionButton
import es.dmoral.toasty.Toasty
import guepardoapps.mynoteencrypted.R
import guepardoapps.mynoteencrypted.controller.DatabaseController
import guepardoapps.mynoteencrypted.controller.DialogController
import guepardoapps.mynoteencrypted.controller.IDialogController
import guepardoapps.mynoteencrypted.model.Note

class ActivityAdd : Activity() {

    private val dialogController: IDialogController = DialogController(this)

    private lateinit var editContent: EditText

    private lateinit var editTitle: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_add)

        editTitle = findViewById(R.id.addTitle)
        editContent = findViewById(R.id.addContent)
        editContent.setScroller(Scroller(this))
        editContent.movementMethod = ScrollingMovementMethod()

        findViewById<FloatingActionButton>(R.id.btnSave).setOnClickListener {
            saveNote()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (editTitle.text.toString().isNotEmpty() || editContent.text.toString().isNotEmpty()) {
                dialogController.createDialog(getString(R.string.unsavedChangesWarning), getString(R.string.yes), this::saveNote, getString(R.string.no), this::finish)
            } else {
                finish()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun saveNote() {
        if (editTitle.text.toString().isEmpty()) {
            Toasty.error(this, getString(R.string.missingTitle), Toast.LENGTH_SHORT).show()
            return
        }

        if (editContent.text.toString().isEmpty()) {
            Toasty.error(this, getString(R.string.missingNote), Toast.LENGTH_SHORT).show()
            return
        }

        if (DatabaseController.instance.add(Note(id = ULID.random(), title = editTitle.text.toString(), content = editContent.text.toString())) == 0L) {
            Toasty.error(this, getString(R.string.saveFailedToasty), Toast.LENGTH_LONG).show()
            dialogController.createDialog(getString(R.string.saveFailedDialog), getString(R.string.retry), this::saveNote, getString(R.string.cancel), this::finish)
        } else {
            finish()
        }
    }
}