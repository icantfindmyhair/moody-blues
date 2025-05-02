package my.edu.utar.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.EventDay;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView selectedDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        selectedDateText = findViewById(R.id.selectedDateText);

        // Handle date clicks
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDay = eventDay.getCalendar();

            int year = clickedDay.get(Calendar.YEAR);
            int month = clickedDay.get(Calendar.MONTH);
            int day = clickedDay.get(Calendar.DAY_OF_MONTH);

            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
            selectedDateText.setText(selectedDate);


            showMoodEntryForDate(selectedDate);
        });

        // Show today’s log by default
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);
        String todayDate = String.format("%04d-%02d-%02d", year, month + 1, day);
        showMoodEntryForDate(todayDate);
        selectedDateText.setText(todayDate);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void showMoodEntryForDate(String selectedDate) {
        SharedPreferences prefs = getSharedPreferences("MoodLoggingPrefs", MODE_PRIVATE);
        String raw = prefs.getString("history", "[]");

        //TextView textView = findViewById(R.id.selectedEntryText);
        StringBuilder displayText = new StringBuilder();


        try {
            JSONArray historyArray = new JSONArray(raw);

            // Clear the views before adding new ones
            LinearLayout historyLayout = findViewById(R.id.logButtonsLayout);
            historyLayout.removeAllViews();

            for (int i = 0; i < historyArray.length(); i++) {
                JSONObject entry = historyArray.getJSONObject(i);
                String timestamp = entry.optString("timestamp");
                String moodKey   = entry.optString("moodKey");
                String weather   = entry.optString("weather");
                String moodInput = entry.optString("moodInput");

                int iconRes = R.drawable.excited_icon; // fallback

                // Set the appropriate icon based on the mood
                switch (moodKey.toLowerCase()) {
                    case "excited":
                        iconRes = R.drawable.excited_icon;
                        break;
                    case "happy":
                        iconRes = R.drawable.happy_icon;
                        break;
                    case "meh":
                        iconRes = R.drawable.meh_icon;
                        break;
                    case "sad":
                        iconRes = R.drawable.sad_icon;
                        break;
                    case "upset":
                        iconRes = R.drawable.upset_icon;
                        break;
                }

                // Check if the timestamp matches the selected date
                if (timestamp.startsWith(formatDateForTimestamp(selectedDate))) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    Button moodButton = (Button) inflater.inflate(R.layout.mood_button, null, false);

                    // Format the time (removes seconds)
                    String[] timeParts = timestamp.split(" ")[3].split(":");
                    String timeWithoutSeconds = timeParts[0] + ":" + timeParts[1];
                    String time = timeWithoutSeconds + " " + timestamp.split(" ")[4];  // AM/PM part

                    // Set the button text
                    moodButton.setText("You felt " + moodKey.toLowerCase() + " at " + time);

                    // Set the icon for the button
                    Drawable icon = ContextCompat.getDrawable(this, iconRes);
                    if (icon != null) {
                        icon.setBounds(0, 0, 150, 150); // Resize icon if necessary
                        moodButton.setCompoundDrawables(icon, null, null, null); // Set icon to the left
                    }

                    // Set up the button click listener to show full mood history
                    moodButton.setOnClickListener(v -> {
                        String fullHistory = "🕒 " + timestamp + "\n"
                                + "Mood: " + moodKey + "\n"
                                + "Note: " + moodInput + "\n"
                                + "Weather: " + weather + "\n";

                        // Show the full history - for example, using a Toast
                        Toast.makeText(this, fullHistory, Toast.LENGTH_LONG).show();
                    });

                    // Set layout parameters for button
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,  // Full width
                            LinearLayout.LayoutParams.WRAP_CONTENT  // Button height
                    );
                    params.setMargins(50, 10, 50, 10); // Add some margins

                    // Apply layout params to the button
                    moodButton.setLayoutParams(params);

                    // Add the button to the layout
                    historyLayout.addView(moodButton);
                }
            }

            // If no buttons were added, show a message
            if (historyLayout.getChildCount() == 0) {
                displayText.append("No mood logs for ").append(selectedDate);
            }

        } catch (JSONException e) {
            // Handle errors if parsing the JSON fails
            displayText.append("Error reading mood history.");
        }

    }


    private String formatDateForTimestamp(String yyyyMMdd) {
        // Convert "2025-05-01" -> "May 1, 2025" to match your SharedPref timestamp format
        String[] parts = yyyyMMdd.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int day = Integer.parseInt(parts[2]);

        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        return monthNames[month] + " " + day + ", " + year;
    }
}
