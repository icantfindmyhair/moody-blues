package my.edu.utar.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

                int iconRes = R.drawable.excited_icon;

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

                if (timestamp.startsWith(formatDateForTimestamp(selectedDate))) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    Button moodButton = (Button) inflater.inflate(R.layout.mood_button, null, false);

                    String[] timeParts = timestamp.split(" ")[3].split(":");
                    String timeWithoutSeconds = timeParts[0] + ":" + timeParts[1];
                    String time = timeWithoutSeconds + " " + timestamp.split(" ")[4];

                    moodButton.setText("You felt " + moodKey.toLowerCase() + " at " + time);

                    Drawable icon = ContextCompat.getDrawable(this, iconRes);
                    if (icon != null) {
                        icon.setBounds(0, 0, 150, 150);
                        moodButton.setCompoundDrawables(icon, null, null, null);
                    }

                    moodButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        View dialogView = inflater.inflate(R.layout.dialog_mood_log, null);

                        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                        TextView dialogContent = dialogView.findViewById(R.id.dialogContent);
                        EditText editMoodInput = dialogView.findViewById(R.id.editMoodInput);
                        Button editButton = dialogView.findViewById(R.id.editButton);

                        String fullHistory = "Weather: " + weather + "\n" + "Mood: " + moodKey + "\n";

                        dialogTitle.setText(timestamp);
                        dialogContent.setText(fullHistory);
                        editMoodInput.setText(moodInput);
                        editMoodInput.setEnabled(false);

                        builder.setView(dialogView);
                        builder.setNegativeButton("Close", null);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        editButton.setOnClickListener(view -> {
                            if (!editMoodInput.isEnabled()) {
                                editMoodInput.setEnabled(true);
                                editMoodInput.requestFocus();
                                editButton.setText("Save");
                            } else {
                                String newInput = editMoodInput.getText().toString();

                                try {
                                    SharedPreferences.Editor editor = prefs.edit();
                                    for (int j = 0; j < historyArray.length(); j++) {
                                        JSONObject obj = historyArray.getJSONObject(j);
                                        if (obj.optString("timestamp").equals(timestamp)) {
                                            obj.put("moodInput", newInput);
                                            break;
                                        }
                                    }
                                    editor.putString("history", historyArray.toString());
                                    editor.apply();

                                    Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
                                    editMoodInput.setEnabled(false);
                                    editButton.setText("Edit log");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Failed to update note.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);

                        closeButton.setOnClickListener(editV -> {
                            dialog.dismiss();
                        });
                    });

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(50, 10, 50, 10);

                    moodButton.setLayoutParams(params);

                    historyLayout.addView(moodButton);
                }
            }

            if (historyLayout.getChildCount() == 0) {
                displayText.append("No mood logs for ").append(selectedDate);
            }

        } catch (JSONException e) {
            displayText.append("Error reading mood history.");
        }

    }


    private String formatDateForTimestamp(String yyyyMMdd) {
        String[] parts = yyyyMMdd.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int day = Integer.parseInt(parts[2]);

        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        return monthNames[month] + " " + day + ", " + year;
    }
}
