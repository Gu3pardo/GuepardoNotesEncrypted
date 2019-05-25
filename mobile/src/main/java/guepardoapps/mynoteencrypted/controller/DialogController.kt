package guepardoapps.mynoteencrypted.controller

import android.content.Context
import com.rey.material.app.Dialog
import com.rey.material.app.ThemeManager
import guepardoapps.mynoteencrypted.R

internal class DialogController(val context: Context) : IDialogController {
    override fun createDialog(title: String, okText: String, okCallback: () -> Unit, cancelText: String, cancelCallback: () -> Unit) {
        val dialog = Dialog(context)
        dialog
                .title(title)
                .positiveAction(okText)
                .negativeAction(cancelText)
                .applyStyle(if (ThemeManager.getInstance().currentTheme == 0) R.style.SimpleDialogLight else R.style.SimpleDialog)
                .setCancelable(true)

        dialog.positiveActionClickListener {
            okCallback()
            dialog.dismiss()
        }

        dialog.negativeActionClickListener {
            cancelCallback()
            dialog.dismiss()
        }

        dialog.show()
    }
}