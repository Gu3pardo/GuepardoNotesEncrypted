package guepardoapps.mynoteencrypted.controller

internal interface ISharedPreferenceController {
    fun erase()

    fun <T : Any> load(key: String, defaultValue: T): T

    fun remove(key: String)

    fun <T : Any> save(key: String, value: T)
}