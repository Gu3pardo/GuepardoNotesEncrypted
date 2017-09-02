package guepardoapps.mynoteencrypted.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rey.material.widget.FloatingActionButton;

import es.dmoral.toasty.Toasty;
import guepardoapps.mynoteencrypted.R;
import guepardoapps.mynoteencrypted.common.constants.*;
import guepardoapps.mynoteencrypted.controller.DatabaseController;
import guepardoapps.mynoteencrypted.controller.NavigationController;
import guepardoapps.mynoteencrypted.controller.ReceiverController;
import guepardoapps.mynoteencrypted.customadapter.NoteListAdapter;
import guepardoapps.mynoteencrypted.handler.PromptForLoginHandler;
import guepardoapps.mynoteencrypted.tools.Logger;

public class ActivityNotes extends Activity {
    private static final String TAG = ActivityNotes.class.getSimpleName();
    private Logger _logger;

    private boolean _created;

    private ListView _listView;
    private ProgressBar _progressBar;

    private Context _context;

    private DatabaseController _databaseController;
    private NavigationController _navigationController;
    private ReceiverController _receiverController;

    private BroadcastReceiver _noteDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _listView.setAdapter(new NoteListAdapter(_context, _databaseController.GetNotes()));
        }
    };

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_main);

        _logger = new Logger(TAG, Enables.LOGGING);
        _logger.Debug("onCreate");

        _context = this;

        _databaseController = DatabaseController.getInstance();
        _navigationController = new NavigationController(_context);
        _receiverController = new ReceiverController(_context);

        String passphrase = getIntent().getStringExtra(Bundles.PASSPHRASE);
        if (passphrase == null) {
            Toasty.error(_context, "Error using database!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        _databaseController.Initialize(_context, passphrase);

        _listView = findViewById(R.id.listView);
        _progressBar = findViewById(R.id.progressBar);

        FloatingActionButton btnImpressum = findViewById(R.id.btnImpressum);
        btnImpressum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("btnImpressum onClick");
                _navigationController.NavigateTo(ActivityImpressum.class, false);
            }
        });

        FloatingActionButton btnAdd = findViewById(R.id.goToAddView);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _logger.Debug("btnAdd onClick");
                _navigationController.NavigateTo(ActivityAdd.class, false);
            }
        });

        _created = true;
        _promptForLogin = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        _logger.Debug("onPause");
        _receiverController.Dispose();
        _promptForLoginHandler.postDelayed(_promptForLoginRunnable, 5 * 60 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.Debug("onResume");

        _receiverController.RegisterReceiver(_noteDeletedReceiver, new String[]{Broadcasts.NOTE_DELETED});

        if (_created) {
            _listView.setAdapter(new NoteListAdapter(_context, _databaseController.GetNotes()));

            _progressBar.setVisibility(View.GONE);
            _listView.setVisibility(View.VISIBLE);
        }

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
        _databaseController.Dispose();
        _receiverController.Dispose();
    }
}