package jp.co.integrityworks.mysiminfo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TelViewModel : ViewModel() {
    private val _line1Number = MutableLiveData("")
    private val _simCountryIso = MutableLiveData("")
    private val _simSerialNumber = MutableLiveData("")
    private val _deviceId = MutableLiveData("")
    private val _androidId = MutableLiveData("")
    private val _simOperator = MutableLiveData("")
    private val _simOperatorName = MutableLiveData("")
    private val _simState = MutableLiveData("")
    private val _voiceMailNumber = MutableLiveData("")

    val line1Number: LiveData<String> = _line1Number
    val simCountryIso: LiveData<String> = _simCountryIso
    val simSerialNumber: LiveData<String> = _simSerialNumber
    val deviceId: LiveData<String> = _deviceId
    val androidId: LiveData<String> = _androidId
    val simOperator: LiveData<String> = _simOperator
    val simOperatorName: LiveData<String> = _simOperatorName
    val simState: LiveData<String> = _simState
    val voiceMailNumber: LiveData<String> = _voiceMailNumber

    @SuppressLint("MissingPermission", "HardwareIds")
    fun initParameters(context: Context) {
        //端末情報取得クラス:TelephonyManager生成
        val telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        var hasC = telMgr.hasCarrierPrivileges()

        //電話番号
        _line1Number.value = telMgr.line1Number
        //SIM国別コード
        _simCountryIso.value = telMgr.simCountryIso
        //SIMシリアルナンバー
        // 携帯端末固有ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            _simSerialNumber.value = "Not supported on iOS 10 or higher"
        } else {
            _simSerialNumber.value = telMgr.simSerialNumber
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _deviceId.value = "Not supported on iOS 10 or higher"
        } else {
            _deviceId.value = telMgr.deviceId
        }
        //AndroidIDの取得
        _androidId.value =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        // MCC+MNC
        _simOperator.value = telMgr.simOperator
        // サービスプロバイダ名
        _simOperatorName.value = telMgr.simOperatorName
        // SIM状態
        _simState.value = telMgr.simState.toString()
        // ボイスメールナンバー
        _voiceMailNumber.value = telMgr.voiceMailNumber
        // READ_PRIVILEGED_PHONE_STATE 必要
        //       relativeLayout.addView(makeTextView("携帯端末固有(IMEI):${telMgr.imei}"))
//        binding.telData = listOf(telMgr.line1Number)
    }
}
