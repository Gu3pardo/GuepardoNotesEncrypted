package guepardoapps.guepardonotesencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import guepardoapps.guepardonotesencrypted.R;
import guepardoapps.guepardonotesencrypted.common.Colors;
import guepardoapps.guepardonotesencrypted.common.Enables;

import guepardoapps.library.toolset.common.Logger;
import guepardoapps.library.toolset.controller.MailController;
import guepardoapps.library.toolset.controller.NavigationController;

public class ActivityImpressum extends Activity {

	private static final String TAG = ActivityImpressum.class.getSimpleName();
	private Logger _logger;

	private Context _context;
	private MailController _mailController;
	private NavigationController _navigationController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.side_impressum);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Colors.ACTION_BAR));

		_logger = new Logger(TAG, Enables.DEBUGGING);
		_logger.Debug("onCreate");

		_context = this;
		_mailController = new MailController(_context);
		_navigationController = new NavigationController(_context);
	}

	public void SendMail(View view) {
		_logger.Debug("SendMail");
		_mailController.SendMail("guepardoapps@gmail.com", true);
	}

	public void GoToGithub(View view) {
		_logger.Debug("GoToGithub");
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Gu3pardo/GuepardoNotesEncrypted/")));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		_logger.Debug("onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			_navigationController.NavigateTo(ActivityNotes.class, true);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}