package my.edu.utar.assignment2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MoodLogging extends AppCompatActivity {
    private ImageView moodIcon;
    private EditText moodInput;
    private TextView moodText;
    private Button doneButton;
    private String moodKey;

    private String weatherDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_loggin);

        // Retrieve moodKey
        moodKey = getIntent().getStringExtra("EXTRA_MOOD_KEY");
        weatherDesc = getIntent().getStringExtra("weather_desc");

        moodIcon   = findViewById(R.id.moodIcon);
        moodInput  = findViewById(R.id.moodInput);
        moodText   = findViewById(R.id.moodText);
        doneButton = findViewById(R.id.doneButton);

        // Pre-fill UI
        int iconResId = getIntent().getIntExtra("mood_icon", R.drawable.excited_icon);
        String text   = getIntent().getStringExtra("mood_text");
        moodIcon.setImageResource(iconResId);
        moodText.setText(text);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        doneButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MoodLoggingPrefs", MODE_PRIVATE);
            String raw = prefs.getString("history", "[]");
            JSONArray arr;
            try {
                arr = new JSONArray(raw);
            } catch (Exception e) {
                arr = new JSONArray();
            }

            //Build the new entry
            JSONObject entry = new JSONObject();
            try {
                String now = java.text.DateFormat
                        .getDateTimeInstance()
                        .format(new java.util.Date());
                entry.put("timestamp", now);
                entry.put("weather", weatherDesc);
                entry.put("moodKey", moodKey);
                entry.put("moodInput", moodInput.getText().toString());
            } catch (JSONException je) {
            }
            arr.put(entry);

            //Save the whole array back to prefs
            prefs.edit()
                    .putString("history", arr.toString())
                    .apply();

            //Yes/No dialog
            YesNoDialogBox.show(
                    MoodLogging.this,
                    "Mood recorded!",
                    "Would you like to generate a playlist based on your mood?",
                    "Yes","No",
                    yesView -> {
                        Intent i = new Intent(MoodLogging.this, GeneratingPlaylistActivity.class);
                        i.putExtra("EXTRA_MOOD_KEY", moodKey);
                        i.putExtra("weather_desc", weatherDesc);
                        startActivity(i);
                        finish();
                    },
                    noView -> finish()
            );
        });

    }
}