package jp.co.integrityworks.mysiminfo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(
                this,android.Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
            // 許可されている時の処理
            getDisplay();
        }else{
            // 拒否されている時の処理
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                //拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 0);
            } else {
                //まだ許可を求める前の時、許可を求めるダイアログを表示します。
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: { //ActivityCompat#requestPermissions()の第2引数で指定した値
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //許可された場合の処理
                    getDisplay();
                }else{
                    //拒否された場合の処理
                }
                break;
            }
        }
    }

    private TextView Make_TextView(String sTitle){
        TextView oTv = new TextView(this);
        oTv.setText(sTitle);
        return oTv;
    }

    public void getDisplay() {
        //レイアウトの生成
        LinearLayout oLayout = new LinearLayout(getApplicationContext());
        //上から下にパーツを組み込む
        oLayout.setOrientation(LinearLayout.VERTICAL);
        //画面の設定
        setContentView(oLayout);

        //端末情報取得クラス:TelephonyManager生成
        TelephonyManager telMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);


        if (null==telMgr) return;
        //デヴァイスID
        //String deviceid = telMgr.getDeviceId();
        //電話番号
        String phoneNumber = telMgr.getLine1Number();
        //SIM国別コード
        String simCountryIso = telMgr.getSimCountryIso();
        //SIMシリアルナンバー
        String simSerialNumber = telMgr.getSimSerialNumber();
        // 携帯端末固有ID
        String deviceId = telMgr.getDeviceId();

        //AndroiIDの取得
        String sAndroid = Settings.Secure.getString(this.getContentResolver(), Settings.System.ANDROID_ID);
        oLayout.addView(Make_TextView("Phone Number:" + phoneNumber));
        oLayout.addView(Make_TextView("SIM国別コード:" + simCountryIso));
        oLayout.addView(Make_TextView("SIMシリアルナンバー:" + simSerialNumber));
        oLayout.addView(Make_TextView("携帯端末固有(IMEI):" + deviceId));
        oLayout.addView(Make_TextView("Android ID:" + sAndroid));
        oLayout.addView(Make_TextView("MCC+MNC:" + telMgr.getSimOperator()));
        oLayout.addView(Make_TextView("サービスプロバイダ名:" + telMgr.getSimOperatorName()));
        oLayout.addView(Make_TextView("SIM状態:" + telMgr.getSimState()));
        oLayout.addView(Make_TextView("ボイスメールナンバー:" + telMgr.getVoiceMailNumber()));
    }
}