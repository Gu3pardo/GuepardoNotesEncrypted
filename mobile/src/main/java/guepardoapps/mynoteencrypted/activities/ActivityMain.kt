package guepardoapps.mynoteencrypted.activities

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import guepardoapps.mynoteencrypted.R
import guepardoapps.mynoteencrypted.controller.DatabaseController
import guepardoapps.mynoteencrypted.controller.NavigationController
import guepardoapps.mynoteencrypted.customadapter.NoteListAdapter

@ExperimentalUnsignedTypes
class ActivityMain : Activity() {

    private val databaseController: DatabaseController = DatabaseController.instance

    private var activityCreated: Boolean = false

    private lateinit var listView: ListView

    private lateinit var progressBar: ProgressBar

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_main)

        listView = findViewById(R.id.listView)
        progressBar = findViewById(R.id.progressBar)

        val navigationController = NavigationController(this)
        findViewById<View>(R.id.btnAbout).setOnClickListener { navigationController.navigate(ActivityAbout::class.java, false) }
        findViewById<View>(R.id.goToAddView).setOnClickListener { navigationController.navigate(ActivityAdd::class.java, false) }

        activityCreated = true
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseController.dispose()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()

        if (activityCreated) {
            reload()
        }
    }

    private fun reload() {
        listView.adapter = NoteListAdapter(this, DatabaseController.instance.get().toTypedArray()) { reload() }
        progressBar.visibility = View.GONE
        listView.visibility = View.VISIBLE
    }
}