package guepardoapps.mynoteencrypted.handler;

import android.os.Handler;

import guepardoapps.mynoteencrypted.tools.Logger;

public class PromptForLoginHandler extends Handler {
    private static final String TAG = PromptForLoginHandler.class.getSimpleName();
    private Logger _logger;

    private static PromptForLoginHandler SINGLETON = new PromptForLoginHandler();

    private PromptForLoginHandler() {
        _logger = new Logger(TAG);
    }

    public static PromptForLoginHandler getInstance() {
        return SINGLETON;
    }
}
