package my.edu.utar.assignment2;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.ImageView;

public class MoodLogging extends AppCompatActivity {
    private ImageView moodIcon;
    private EditText moodInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_loggin);

        moodIcon = findViewById(R.id.moodIcon);
        moodInput = findViewById(R.id.moodInput);

        // Get the image resource passed through the Intent
        Intent intent = getIntent();
        int iconResId = intent.getIntExtra("mood_icon", R.drawable.excited_icon); // Default to excited icon
        moodIcon.setImageResource(iconResId);
    }
}