package guepardoapps.mynoteencrypted.extensions

/**
 * @param divider the value which shall be used to divide the char by
 * @return returns a new char out of the division of the original char and the divider
 */
internal fun Char.div(divider: Int): Char = (this.toInt() / divider).toChar()