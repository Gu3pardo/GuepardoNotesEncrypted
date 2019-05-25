package guepardoapps.mynoteencrypted.utils

import androidx.annotation.NonNull

internal fun checkValidity(@NonNull password: String) : Boolean {
    var charBigCount = 0
    var charSmallCount = 0
    var numberCount = 0
    var signCount = 0

    for (entry in password.toCharArray()) {
        when (entry) {
            in '0'..'9' -> numberCount++
            in 'a'..'z' -> charSmallCount++
            in 'A'..'Z' -> charBigCount++
            else -> signCount++
        }
    }

    if (charBigCount > 0) {
        if (charSmallCount > 0) {
            if (numberCount > 0) {
                if (signCount > 0) {
                    return true
                } else {
                    Logger.instance.warning("checkValidity", "Too less signs!")
                }
            } else {
                Logger.instance.warning("checkValidity", "Too less numbers!")
            }
        } else {
            Logger.instance.warning("checkValidity", "Too less small chars!")
        }
    } else {
        Logger.instance.warning("checkValidity", "Too less big signs!")
    }

    return false
}
