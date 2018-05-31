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

    static String imeiStr = "null";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    protected void onResume() {
        super.onResume();
        TextView imei = (TextView) findViewById(R.id.imei_text);

        // Get device IMEI or show null if not allowed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }

        String imeiLabel = getResources().getString(R.string.imei_text);
        imei.setText(imeiLabel + " " + imeiStr);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                    imeiStr = tm.getDeviceId();
                }

                return;
            }
        }
    }
}
