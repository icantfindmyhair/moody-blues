package my.edu.utar.assignment2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MoodLogging extends AppCompatActivity {

    private ImageView moodIcon;
    private EditText moodInput;
    private TextView moodText;
    private Button doneButton;
    private String moodKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_loggin);

        // Retrieve moodKey
        moodKey = getIntent().getStringExtra("EXTRA_MOOD_KEY");

        moodIcon = findViewById(R.id.moodIcon);
        moodInput = findViewById(R.id.moodInput);
        moodText = findViewById(R.id.moodText);
        doneButton = findViewById(R.id.doneButton);

        SharedPreferences sharedPreferences = getSharedPreferences("MoodLoggingPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int iconResId = getIntent().getIntExtra("mood_icon", R.drawable.excited_icon);
        String text    = getIntent().getStringExtra("mood_text");
        moodIcon.setImageResource(iconResId);
        moodText.setText(text);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // -------------- after click done, store data and show alert box ----------------
        // no to return home, yes to generate playlist
        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            // Save entry to SharedPreferences (omitted details)
            YesNoDialogBox.show(
                    MoodLogging.this,
                    "Mood recorded!",
                    "Would you like to generate a playlist based on your mood?",
                    "Yes", "No",
                    yesView -> {
                        Intent i = new Intent(MoodLogging.this, GeneratingPlaylistActivity.class);
                        i.putExtra("EXTRA_MOOD_KEY", moodKey);
                        startActivity(i);
                        finish();
                    },
                    noView -> finish()
            );
        });
    }
}
