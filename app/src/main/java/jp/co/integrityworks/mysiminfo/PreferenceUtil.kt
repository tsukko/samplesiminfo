package jp.co.integrityworks.mysiminfo

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PreferenceUtil
/**
 * コンストラクタ
 * @param context 自身のContext
 * @since 001
 */
(context: Context) {
    private val mSharedPreferences: SharedPreferences
    var PREFERENCES_KEY = "PREFERENCES_KEY"
    var preference: String?
        get() = mSharedPreferences.getString(PREFERENCES_KEY, null)
        set(name) = mSharedPreferences.edit().putString(PREFERENCES_KEY, name).apply()

    init {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }
}
