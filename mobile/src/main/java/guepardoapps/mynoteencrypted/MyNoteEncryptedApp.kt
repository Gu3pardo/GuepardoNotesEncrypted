package guepardoapps.mynoteencrypted

import android.app.Application
import guepardoapps.mynoteencrypted.controller.DatabaseController
import guepardoapps.mynoteencrypted.utils.Logger

class MyNoteEncryptedApp : Application() {
    private val tag: String = MyNoteEncryptedApp::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        Logger.instance.initialize(this)
        Logger.instance.debug(tag, "onCreate")
    }
}