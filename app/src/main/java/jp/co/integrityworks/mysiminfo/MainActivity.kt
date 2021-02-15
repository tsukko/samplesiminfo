package jp.co.integrityworks.mysiminfo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val REQUEST_MULTI_PERMISSIONS = 200
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TelViewModel
    //    private val mAd: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug(TAG, "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title =
                if (BuildConfig.DEBUG) getString(R.string.app_name) + " (deb)" else getString(R.string.app_name)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(TelViewModel::class.java)

//        val checkPermission: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Manifest.permission.READ_PHONE_NUMBERS
//        } else {
//            Manifest.permission.READ_PHONE_STATE
//        }
//        if (checkSelfPermission(checkPermission) == PackageManager.PERMISSION_GRANTED) {
//            // 許可されている
//            viewModel.initParameters(applicationContext) // 初期表示時のデータ処理
//        } else {
//            // 許可されていないので許可ダイアログを表示する
//            requestPermissions(
//                    arrayOf(checkPermission),
//                    PERMISSIONS_REQUEST_CODE
//            )
//        }
//        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            // 許可されている
////            viewModel.initParameters(applicationContext) // 初期表示時のデータ処理
//        } else {
//            // 許可されていないので許可ダイアログを表示する
//            requestPermissions(
//                    arrayOf(Manifest.permission.READ_PHONE_STATE),
//                    PERMISSIONS_REQUEST_CODE
//            )
//        }
        checkMultiPermissions()

        binding.telViewModel = viewModel

        MobileAds.initialize(this, BuildConfig.admob_app_id)
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        // ad's lifecycle: loading, opening, closing, and so on
        binding.adView.adListener = object : AdListener() {
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
        val reqPermissions: ArrayList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                reqPermissions.add(Manifest.permission.READ_PHONE_NUMBERS)
            }
        }
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            reqPermissions.add(Manifest.permission.READ_PHONE_STATE)
        }

        // 未許可
        if (reqPermissions.isNotEmpty()) {
            requestPermissions(
                    reqPermissions.toArray(arrayOfNulls(0)),
                    REQUEST_MULTI_PERMISSIONS)
        } else {
            viewModel.initParameters(applicationContext) // 初期表示時のデータ処理
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
                    binding.telViewModel?.initParameters(applicationContext)
//                        if (permissions[0] == Manifest.permission.READ_PHONE_STATE) {
//
//                        }
//                    else {
//                            Logger.debug(TAG, "aaaaaaaaaaaaaa")
//                        }
                } else {
                    //拒否された場合の処理
                    Handler(Looper.getMainLooper()).post {
                        RuntimePermissionUtils().showAlertDialog(
                                supportFragmentManager,
                                "READ_PHONE_STATE"
                        )
                    }
                }
            }
            REQUEST_MULTI_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var activeNum = 0
                    for (i in permissions.indices) {
                        if (permissions[i] == Manifest.permission.READ_PHONE_NUMBERS) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                Logger.debug(TAG, "READ_PHONE_NUMBERS: GRANTED")
                                activeNum++
                            } else {
                                // 拒否された時の対応
                                Handler(Looper.getMainLooper()).post {
                                    RuntimePermissionUtils().showAlertDialog(
                                            supportFragmentManager,
                                            "READ_PHONE_NUMBERS"
                                    )
                                }
                            }
                        } else if (permissions[i] == Manifest.permission.READ_PHONE_STATE) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                Logger.debug(TAG,"READ_PHONE_STATE: GRANTED")
                                activeNum++
                            } else {
                                // 拒否された時の対応
                                Handler(Looper.getMainLooper()).post {
                                    RuntimePermissionUtils().showAlertDialog(
                                            supportFragmentManager,
                                            "READ_PHONE_STATE"
                                    )
                                }
                            }
                        }
                    }
                    if (activeNum == 2) {
                        binding.telViewModel?.initParameters(applicationContext)
                    }
                }
            }
        }
    }
}