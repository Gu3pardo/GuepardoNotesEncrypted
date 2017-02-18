package guepardoapps.guepardonotesencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Colors;
import guepardoapps.guepardonotesencrypted.common.SharedPrefConstants;
import guepardoapps.guepardonotesencrypted.controller.NotesDialogController;

import guepardoapps.toolset.controller.SharedPrefController;
import guepardoapps.toolset.services.NavigationService;

public class ActivityBoot extends Activity {

	private Context _context;

	private NavigationService _navigationService;
	private NotesDialogController _dialogController;
	private SharedPrefController _sharedPrefController;

	private Runnable _navigateToMainRunnable = new Runnable() {
		@Override
		public void run() {
			_navigationService.NavigateTo(ActivityNotes.class, true);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_boot);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_context = this;
		_dialogController = new NotesDialogController(_context);
		_navigationService = new NavigationService(_context);
		_sharedPrefController = new SharedPrefController(_context, SharedPrefConstants.SHARED_PREF_NAME);

		if (!_sharedPrefController.LoadBooleanValueFromSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME)) {
			_dialogController.ShowDialogFirstLogin(_navigateToMainRunnable);
		} else {
			_dialogController.ShowDialogLogin(_navigateToMainRunnable);
		}
	}

	protected void onResume() {
		super.onResume();
		_navigationService.NavigateTo(ActivityNotes.class, true);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}