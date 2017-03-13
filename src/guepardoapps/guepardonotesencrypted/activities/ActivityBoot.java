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

import guepardoapps.toolset.common.Logger;
import guepardoapps.toolset.controller.NavigationController;
import guepardoapps.toolset.controller.SharedPrefController;

public class ActivityBoot extends Activity {

	private static final String TAG = ActivityBoot.class.getSimpleName();
	private Logger _logger;

	private Context _context;

	private NavigationController _navigationController;
	private NotesDialogController _dialogController;
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
		_dialogController = new NotesDialogController(_context);
		_navigationController = new NavigationController(_context);
		_sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

		if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME)) {
			_dialogController.ShowDialogFirstLogin(_navigateToMainRunnable);
		} else {
			_dialogController.ShowDialogLogin(_navigateToMainRunnable);
		}
	}

	protected void onResume() {
		super.onResume();
		_logger.Debug("onResume");
		_navigationController.NavigateTo(ActivityNotes.class, true);
	}
}