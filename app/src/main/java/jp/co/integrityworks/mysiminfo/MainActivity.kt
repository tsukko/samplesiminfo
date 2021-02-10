package jp.co.integrityworks.mysiminfo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import jp.co.integrityworks.mysiminfo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val PERMISSIONS_REQUEST_CODE = 100
    private var binding: ActivityMainBinding? = null
    //    private val mAd: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug(javaClass.simpleName, "onCreate")
        supportActionBar?.title =
                if (BuildConfig.DEBUG) getString(R.string.app_name) + " (deb)" else getString(R.string.app_name)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(TelViewModel::class.java)

        // TODO パーミッション許可周りの実装を見直す、今は以下のエラーが発生する
        // Caused by: java.lang.SecurityException: getLine1NumberForDisplay: Neither user 10298 nor current process has android.permission.READ_PHONE_STATE, android.permission.READ_SMS, or android.permission.READ_PHONE_NUMBERS
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // 許可されている
            viewModel.initParameters(applicationContext) // 初期表示時のデータ処理
        } else {
            // 許可されていないので許可ダイアログを表示する
            requestPermissions(
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    PERMISSIONS_REQUEST_CODE
            )
        }

        binding?.telViewModel = viewModel

        MobileAds.initialize(this, BuildConfig.admob_app_id)
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding?.adView?.loadAd(adRequest)

        // ad's lifecycle: loading, opening, closing, and so on
        binding?.adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Logger.debug(TAG, "Code to be executed when an ad finishes loading.")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Logger.debug(TAG, "Code to be executed when an ad request fails.")
            }

            override fun onAdOpened() {
                Logger.debug(
                        TAG,
                        "Code to be executed when an ad opens an overlay that covers the screen."
                )
            }

            override fun onAdLeftApplication() {
                Logger.debug(TAG, "Code to be executed when the user has left the app.")
            }

            override fun onAdClosed() {
                Logger.debug(
                        TAG,
                        "Code to be executed when when the user is about to return to the app after tapping on an ad."
                )
            }
        }
    }

    // TODO パーミッション許可周りの実装を見直す
    private fun checkMultiPermissions() {
        // 位置情報の Permission
        val permissionLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // 外部ストレージ書き込みの Permission
        val permissionExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val reqPermissions: ArrayList<String> = ArrayList()

        // 位置情報の Permission が許可されているか確認
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
        } else {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 外部ストレージ書き込みが許可されているか確認
        if (permissionExtStorage == PackageManager.PERMISSION_GRANTED) {
        } else {
            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        // 未許可
        if (!reqPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    reqPermissions.toArray(arrayOfNulls(0)),
                    REQUEST_MULTI_PERMISSIONS)
        } else {
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //許可された場合の処理
                    binding?.telViewModel?.initParameters(applicationContext)
                } else {
                    //拒否された場合の処理
                    Handler().post(Runnable {
                        RuntimePermissionUtils().showAlertDialog(
                                supportFragmentManager,
                                "READ_PHONE_STATE"
                        )
                    })
                }
            }
        }
    }
}