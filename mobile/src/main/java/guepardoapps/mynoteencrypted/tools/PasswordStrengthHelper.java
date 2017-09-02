package guepardoapps.mynoteencrypted.tools;

import android.support.annotation.NonNull;

public class PasswordStrengthHelper {

    private static final String TAG = PasswordStrengthHelper.class.getSimpleName();

    public static boolean CheckValidity(@NonNull String enteredPassword) {
        Logger logger = new Logger(TAG);

        int charBigCount = 0;
        int charSmallCount = 0;
        int numberCount = 0;
        int signCount = 0;

        for (char entry : enteredPassword.toCharArray()) {
            if (entry >= '0' && entry <= '9') {
                numberCount++;
            } else if (entry >= 'a' && entry <= 'z') {
                charSmallCount++;
            } else if (entry >= 'A' && entry <= 'Z') {
                charBigCount++;
            } else {
                signCount++;
            }
        }

        if (charBigCount > 0) {
            if (charSmallCount > 0) {
                if (numberCount > 0) {
                    if (signCount > 0) {
                        return true;
                    } else {
                        logger.Warn("Too less signs!");
                    }
                } else {
                    logger.Warn("Too less numbers!");
                }
            } else {
                logger.Warn("Too less small chars!");
            }
        } else {
            logger.Warn("Too less big chars!");
        }

        return false;
    }
}
