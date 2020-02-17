package jp.co.integrityworks.mysiminfo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    private val mAd: RewardedVideoAd? = null
    // A menu item view type.
    private val MENU_ITEM_VIEW_TYPE = 0

    // The unified native ad view type.
    private val UNIFIED_NATIVE_AD_VIEW_TYPE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug(javaClass.simpleName, "onCreate")
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // 許可されている
            getDisplay()
        } else {
            // 許可されていないので許可ダイアログを表示する
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSIONS_REQUEST_CODE)
        }

        MobileAds.initialize(this, getString(R.string.admob_app_id))
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // ad's lifecycle: loading, opening, closing, and so on
        // ad's lifecycle: loading, opening, closing, and so on
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("debug", "Code to be executed when an ad finishes loading.")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("debug", "Code to be executed when an ad request fails.")
            }

            override fun onAdOpened() {
                Log.d("debug", "Code to be executed when an ad opens an overlay that covers the screen.")
            }

            override fun onAdLeftApplication() {
                Log.d("debug", "Code to be executed when the user has left the app.")
            }

            override fun onAdClosed() {
                Log.d("debug", "Code to be executed when when the user is about to return to the app after tapping on an ad.")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //許可された場合の処理
                    getDisplay()
                } else {
                    //拒否された場合の処理
                    Handler().post(Runnable {
                        RuntimePermissionUtils().showAlertDialog(supportFragmentManager, "READ_PHONE_STATE")
                    })
                }
            }
        }
    }

    private fun makeTextView(sTitle: String): TextView {
        val oTv = TextView(this)
        oTv.text = sTitle
        return oTv
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getDisplay() {
//        //レイアウトの生成
//        val oLayout = LinearLayout(this)
//        //上から下にパーツを組み込む
//        oLayout.orientation = LinearLayout.VERTICAL
//        //画面の設定
//        setContentView(oLayout)

        //端末情報取得クラス:TelephonyManager生成
        val telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //電話番号
        phoneNumber.text = telMgr.line1Number
        //SIM国別コード
        simCountryIso.text = telMgr.simCountryIso
        //SIMシリアルナンバー
        simSerialNumber.text = telMgr.simSerialNumber
        // 携帯端末固有ID
        deviceId.text = telMgr.deviceId
        //AndroidIDの取得
        androidId.text = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        // MCC+MNC
        simOperator.text = telMgr.simOperator
        // サービスプロバイダ名
        simOperatorName.text = telMgr.simOperatorName
        // SIM状態
        simState.text = telMgr.simState.toString()
        // ボイスメールナンバー
        voiceMailNumber.text = telMgr.voiceMailNumber
        // READ_PRIVILEGED_PHONE_STATE 必要
        //       relativeLayout.addView(makeTextView("携帯端末固有(IMEI):${telMgr.imei}"))

    }
}