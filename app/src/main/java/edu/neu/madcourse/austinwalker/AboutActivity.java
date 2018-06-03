package edu.neu.madcourse.austinwalker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private static final int PERMISSION_READ_PHONE_STATE = 0;

    static String imeiStr = "null";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    protected void onResume() {
        super.onResume();
        TextView imei = findViewById(R.id.imei_text);
        String imeiLabel = getResources().getString(R.string.imei_text);

        // Get device IMEI or show null if not allowed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                imeiStr = "Need permission to access";

            } else {
                ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_PHONE_STATE);
            }

        } else {
            imeiStr = fetchIMEI();
        }

        imei.setText(imeiLabel + " " + imeiStr);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imeiStr = fetchIMEI();
                } else {
                    imeiStr = "Permission denied";
                }

                return;
            }
        }
    }

    private String fetchIMEI() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
