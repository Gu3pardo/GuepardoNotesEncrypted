package guepardoapps.mynoteencrypted.extensions

import java.util.*

/**
 * @param digits the numbers to show
 * @return returns a string with specified format and additional zeros
 */
internal fun Int.integerFormat(digits: Int): String = String.format(Locale.getDefault(), "%0${digits}d", this)
