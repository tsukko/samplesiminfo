package jp.co.integrityworks.mysiminfo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var mPreferenceUtil: PreferenceUtil? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.debug(javaClass.getSimpleName(), "onCreate")
        mPreferenceUtil = PreferenceUtil(this)

        Logger.debug(javaClass.getSimpleName(), "Preference:" + mPreferenceUtil!!.preference)
        if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // 許可されている時の処理
            getDisplay()
        } else {
            // 拒否されている時の処理
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                //拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), 0)
            } else {
                //まだ許可を求める前の時、許可を求めるダイアログを表示します。
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), 0)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> { //ActivityCompat#requestPermissions()の第2引数で指定した値
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //許可された場合の処理
                    getDisplay()
                } else {
                    //拒否された場合の処理
                }
            }
        }
    }

    private fun makeTextView(sTitle: String): TextView {
        val oTv = TextView(this)
        oTv.text = sTitle
        return oTv
    }

    override fun onStart() {
        super.onStart()
        Logger.debug(javaClass.getSimpleName(), "onStart")
    }

    override fun onStop() {
        super.onStop()
        Logger.debug(javaClass.getSimpleName(), "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.debug(javaClass.getSimpleName(), "onDestroy")
    }

    override fun onPause() {
        super.onPause()
        Logger.debug(javaClass.getSimpleName(), "onPause")
    }

    override fun onResume() {
        super.onResume()
        Logger.debug(javaClass.getSimpleName(), "onResume")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Logger.debug(javaClass.getSimpleName(), "onSaveInstanceState")
        mPreferenceUtil!!.preference = "test"
    }

    private fun getDisplay() {
        //レイアウトの生成
        val oLayout = LinearLayout(applicationContext)
        //上から下にパーツを組み込む
        oLayout.orientation = LinearLayout.VERTICAL
        //画面の設定
        setContentView(oLayout)

        //端末情報取得クラス:TelephonyManager生成
        val telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


// deviceID

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //String deviceid = telMgr.getDeviceId();
        //電話番号
        val phoneNumber = telMgr.line1Number
        //SIM国別コード
        val simCountryIso = telMgr.simCountryIso
        //SIMシリアルナンバー
        val simSerialNumber = telMgr.simSerialNumber
        // 携帯端末固有ID
        val deviceId = telMgr.deviceId

        //AndroiIDの取得
        val sAndroid = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        oLayout.addView(makeTextView("Phone Number:$phoneNumber"))
        oLayout.addView(makeTextView("SIM国別コード:$simCountryIso"))
        oLayout.addView(makeTextView("SIMシリアルナンバー:$simSerialNumber"))
        oLayout.addView(makeTextView("携帯端末固有(IMEI):$deviceId"))
        oLayout.addView(makeTextView("Android ID:$sAndroid"))
        oLayout.addView(makeTextView("MCC+MNC:" + telMgr.simOperator))
        oLayout.addView(makeTextView("サービスプロバイダ名:" + telMgr.simOperatorName))
        oLayout.addView(makeTextView("SIM状態:" + telMgr.simState))
        oLayout.addView(makeTextView("ボイスメールナンバー:" + telMgr.voiceMailNumber))
    }
}