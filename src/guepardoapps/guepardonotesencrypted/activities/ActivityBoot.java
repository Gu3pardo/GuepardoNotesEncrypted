package guepardoapps.guepardonotesencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Colors;
import guepardoapps.guepardonotesencrypted.common.Enables;
import guepardoapps.guepardonotesencrypted.common.SharedPrefConstants;
import guepardoapps.guepardonotesencrypted.controller.NotesDialogController;

import guepardoapps.library.toolset.common.Logger;
import guepardoapps.library.toolset.controller.NavigationController;
import guepardoapps.library.toolset.controller.SharedPrefController;

public class ActivityBoot extends Activity {

	private static final String TAG = ActivityBoot.class.getSimpleName();
	private Logger _logger;

	private Context _context;

	private NavigationController _navigationController;
	private NotesDialogController _notesDialogController;
	private SharedPrefController _sharedPrefController;

	private Runnable _navigateToMainRunnable = new Runnable() {
		@Override
		public void run() {
			_logger.Debug("_navigateToMainRunnable run");

			_navigationController.NavigateTo(ActivityNotes.class, true);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_boot);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_logger = new Logger(TAG, Enables.DEBUGGING);
		_logger.Debug("onCreate");

		_context = this;
		_notesDialogController = new NotesDialogController(_context);
		_navigationController = new NavigationController(_context);
		_sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

		if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME)) {
			_notesDialogController.ShowDialogFirstLogin(_navigateToMainRunnable);
		} else {
			_notesDialogController.ShowDialogLogin(_navigateToMainRunnable);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		_logger.Debug("onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		_logger.Debug("onResume");
		_navigationController.NavigateTo(ActivityNotes.class, true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_logger.Debug("onDestroy");
		_notesDialogController.Dispose();
	}
}