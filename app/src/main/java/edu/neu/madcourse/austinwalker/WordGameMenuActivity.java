package edu.neu.madcourse.austinwalker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class WordGameMenuActivity extends AppCompatActivity {

    private static final String KEY_RESTORE = "key_restore";
    private static final String PREF_RESTORE = "pref_restore";
    private WordGameFragment mGameFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_game_menu);

        // New game button
        View newButton = findViewById(R.id.scroggle_new_game_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordGameMenuActivity.this, WordGameActivity.class);
                startActivity(intent);
            }
        });

        // Resume game button
        View resumeGame = findViewById(R.id.scroggle_resume_game_button);

        // Acknowledgments button
        View ackButton = findViewById(R.id.scroggle_ack_button);
        ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordGameMenuActivity.this, CreditsActivity.class);
                startActivity(intent);
            }
        });
    }
}
