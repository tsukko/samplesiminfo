package jp.co.integrityworks.mysiminfo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import jp.co.integrityworks.mysiminfo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    private var binding: ActivityMainBinding? = null
    //    private val mAd: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug(javaClass.simpleName, "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.lifecycleOwner = this
        val viewModel = ViewModelProvider(this).get(TelViewModel::class.java)

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

//        MobileAds.initialize(this, BuildConfig.admob_app_id)
//        val adRequest: AdRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
//
//        // ad's lifecycle: loading, opening, closing, and so on
//        adView.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                Log.d("debug", "Code to be executed when an ad finishes loading.")
//            }
//
//            override fun onAdFailedToLoad(errorCode: Int) {
//                Log.d("debug", "Code to be executed when an ad request fails.")
//            }
//
//            override fun onAdOpened() {
//                Log.d(
//                    "debug",
//                    "Code to be executed when an ad opens an overlay that covers the screen."
//                )
//            }
//
//            override fun onAdLeftApplication() {
//                Log.d("debug", "Code to be executed when the user has left the app.")
//            }
//
//            override fun onAdClosed() {
//                Log.d(
//                    "debug",
//                    "Code to be executed when when the user is about to return to the app after tapping on an ad."
//                )
//            }
//        }
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