package edu.neu.madcourse.austinwalker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView imei = (TextView) findViewById(R.id.imei_text);
        String imeiStr = imei.getText().toString();
        imei.setText(imeiStr + "null");
    }
}
