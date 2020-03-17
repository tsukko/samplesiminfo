package jp.co.integrityworks.mysiminfo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import jp.co.integrityworks.mysiminfo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


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
        adView.loadAd(adRequest)

        // ad's lifecycle: loading, opening, closing, and so on
        adView.adListener = object : AdListener() {
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