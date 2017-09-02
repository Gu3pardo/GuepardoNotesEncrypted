package guepardoapps.mynoteencrypted.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import guepardoapps.mynoteencrypted.R;
import guepardoapps.mynoteencrypted.common.constants.*;
import guepardoapps.mynoteencrypted.controller.MailController;
import guepardoapps.mynoteencrypted.controller.NavigationController;
import guepardoapps.mynoteencrypted.handler.PromptForLoginHandler;
import guepardoapps.mynoteencrypted.tools.Logger;

public class ActivityImpressum extends Activity {
    private static final String TAG = ActivityImpressum.class.getSimpleName();
    private Logger _logger;

    private Context _context;
    private MailController _mailController;
    private NavigationController _navigationController;

    private boolean _promptForLogin;
    private PromptForLoginHandler _promptForLoginHandler = PromptForLoginHandler.getInstance();
    private Runnable _promptForLoginRunnable = new Runnable() {
        @Override
        public void run() {
            _logger.Debug("Setting flag to prompt for login!");
            _promptForLogin = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_impressum);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _context = this;
        _mailController = new MailController(_context);
        _navigationController = new NavigationController(_context);

        _promptForLogin = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
        _promptForLoginHandler.postDelayed(_promptForLoginRunnable, 5 * 60 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");

        if (_promptForLogin) {
            Toasty.info(_context, "Due to inactivity you have to login again!", Toast.LENGTH_LONG).show();
            _navigationController.NavigateTo(ActivityLogin.class, true);
        }

        _promptForLoginHandler.removeCallbacks(_promptForLoginRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _logger.Debug("onDestroy");
    }

    public void SendMail(View view) {
        _logger.Debug("SendMail");
        _mailController.SendMail("guepardoapps@gmail.com", true);
    }

    public void GoToGitHub(View view) {
        _logger.Debug("GoToGitHub");
        String gitHubLink = _context.getString(R.string.gitHubLink);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gitHubLink)));
    }

    public void PayPal(View view) {
        _logger.Debug("PayPal");
        String gitHubLink = _context.getString(R.string.payPalLink);
        Intent gitHubBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gitHubLink));
        _context.startActivity(gitHubBrowserIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        _logger.Debug("onKeyDown");

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}