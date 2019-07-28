package guepardoapps.mynoteencrypted.model

import androidx.annotation.NonNull
import guepardoapps.mynoteencrypted.extensions.integerFormat
import java.time.LocalDateTime

internal data class Note(val id: String, @NonNull var title: String, @NonNull var content: String,
                var year: Int, var month: Int, var day: Int,
                var hour: Int, var minute: Int, var second: Int) {

    constructor(id: String, @NonNull title: String, @NonNull content: String)
            : this(id, title, content,
            LocalDateTime.now().year, LocalDateTime.now().month.value, LocalDateTime.now().dayOfMonth,
            LocalDateTime.now().hour, LocalDateTime.now().minute, LocalDateTime.now().second)

    val dateString: String = "${day.integerFormat(2)}.${(month + 1).integerFormat(2)}.${year.integerFormat(4)}"

    val timeString: String = "${hour.integerFormat(2)}.${(minute + 1).integerFormat(2)}.${second.integerFormat(2)}"

    val dateTimeString: String = "$dateString / $timeString"
}