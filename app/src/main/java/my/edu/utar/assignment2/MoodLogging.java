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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_loggin);

        moodIcon = findViewById(R.id.moodIcon);
        moodInput = findViewById(R.id.moodInput);
        moodText = findViewById(R.id.moodText);
        doneButton = findViewById(R.id.doneButton);

        SharedPreferences sharedPreferences = getSharedPreferences("MoodLoggingPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent intent = getIntent();
        int iconResId = intent.getIntExtra("mood_icon", R.drawable.excited_icon);
        String text = intent.getStringExtra("mood_text");

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
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

                String mood = moodInput.getText().toString();

                editor.putString("date", currentDate);
                editor.putString("time", currentDate);
                editor.putString("weather", "");  // empty string for now cuz weather api not done
                editor.putString("mood", mood);
                editor.putString("moodInput", mood);

                editor.apply();

                YesNoDialogBox.show(
                        MoodLogging.this,
                        "Mood recorded!",
                        "Would you like to generate a playlist based on your mood?",
                        "Yes", "No",
                        yesView -> {
                            Intent intent = new Intent(MoodLogging.this, GeneratingPlaylistActivity.class);
                            startActivity(intent);
                        },
                        noView -> finish()
                );


            }
        });
    }
}
