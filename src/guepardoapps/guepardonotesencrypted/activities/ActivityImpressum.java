package guepardoapps.guepardonotesencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Colors;

import guepardoapps.toolset.services.NavigationService;

public class ActivityImpressum extends Activity {

	private Context _context;
	private NavigationService _navigationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_impressum);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_context = this;
		_navigationService = new NavigationService(_context);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			_navigationService.NavigateTo(ActivityNotes.class, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}