package guepardoapps.mynoteencrypted.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import guepardoapps.mynoteencrypted.R
import guepardoapps.mynoteencrypted.controller.DatabaseController
import guepardoapps.mynoteencrypted.controller.NavigationController
import guepardoapps.mynoteencrypted.controller.SharedPreferenceController
import guepardoapps.mynoteencrypted.controller.SystemInfoController
import guepardoapps.mynoteencrypted.customadapter.NoteListAdapter

@ExperimentalUnsignedTypes
class ActivityMain : Activity() {

    private var activityCreated: Boolean = false

    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar

    private lateinit var navigationController: NavigationController
    private lateinit var sharedPreferenceController: SharedPreferenceController
    private lateinit var systemInfoController: SystemInfoController

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_main)

        listView = findViewById(R.id.listView)
        progressBar = findViewById(R.id.progressBar)

        navigationController = NavigationController(this)
        sharedPreferenceController = SharedPreferenceController(this)
        systemInfoController = SystemInfoController(this)

        findViewById<View>(R.id.btnAbout).setOnClickListener { navigationController.navigate(ActivityAbout::class.java, false) }
        findViewById<View>(R.id.goToAddView).setOnClickListener { navigationController.navigate(ActivityAdd::class.java, false) }

        activityCreated = true
    }

    override fun onResume() {
        super.onResume()

        if (activityCreated) {
            listView.adapter = NoteListAdapter(this, DatabaseController.instance.get().toTypedArray())
            progressBar.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }
}