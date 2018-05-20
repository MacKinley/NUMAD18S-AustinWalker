package edu.neu.madcourse.austinwalker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    protected void onResume() {
        super.onResume();
        TextView imei = (TextView) findViewById(R.id.imei_text);
        String imeiStr = imei.getText().toString();
        imei.setText(imeiStr + " " + getDeviceIMEI());
    }

    public String getDeviceIMEI() {
        String identifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: ask for permissions
                return "null";
            }
            identifier = tm.getDeviceId();
        }
        if (null == identifier || 0 == identifier.length()) {
            identifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return identifier;
    }
}
