package my.edu.utar.assignment2;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MoodLogging extends AppCompatActivity {
    private ImageView moodIcon;
    private EditText moodInput;
    private TextView moodText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_loggin);

        moodIcon = findViewById(R.id.moodIcon);
        moodInput = findViewById(R.id.moodInput);
        moodText = findViewById(R.id.moodText);

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

    }
}