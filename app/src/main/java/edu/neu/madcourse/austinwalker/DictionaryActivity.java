package edu.neu.madcourse.austinwalker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DictionaryActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        // Acknowledgments button
        View ackButton = findViewById(R.id.dictionary_ack_button);
        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DictionaryActivity.this, CreditsActivity.class);
                startActivity(intent);
            }
        });
    }
}
