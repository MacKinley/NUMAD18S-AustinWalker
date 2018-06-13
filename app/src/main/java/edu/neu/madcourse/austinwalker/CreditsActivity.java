package edu.neu.madcourse.austinwalker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credits);

        Spinner dropdown = findViewById(R.id.ack_version_dropdown);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Module 5", "Module 3"});
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        CharSequence creditsStr;

        switch (pos) {
            case 0:
                creditsStr = getResources().getText(R.string.ack_text_v5);
                break;

            case 1:
                creditsStr = getResources().getText(R.string.ack_text_v3);
                break;

            default:
                creditsStr = "Invalid selection";
        }

        TextView creditsText = findViewById(R.id.ack_text_view);
        creditsText.setText(creditsStr);
        creditsText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}
