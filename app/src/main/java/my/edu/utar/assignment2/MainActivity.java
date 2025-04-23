package my.edu.utar.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnExcited, btnHappy, btnMeh, btnSad, btnUpset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --------------------- Home screen time and date ---------------------
        TextView dayText = findViewById(R.id.dayText);
        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);
        TextView yearText = findViewById(R.id.yearText);
        TextView monthText = findViewById(R.id.monthText);

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday"};
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October","November","December"};
        String formattedDay = dayNames[dayOfWeek - 1];
        String formattedMonth = monthNames[month];
        String formattedTime = String.format("%02d:%02d", hour, minute);

        yearText.setText(String.valueOf(year));
        monthText.setText(formattedMonth);
        dayText.setText(formattedDay);
        timeText.setText(formattedTime);
        dateText.setText(String.valueOf(day));

        // --------- mood button click -----------
        btnExcited = findViewById(R.id.btnExcited);
        btnHappy = findViewById(R.id.btnHappy);
        btnMeh = findViewById(R.id.btnMeh);
        btnSad = findViewById(R.id.btnSad);
        btnUpset = findViewById(R.id.btnUpset);

        btnExcited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodLoggingActivity(R.drawable.excited_icon, "Yippee! :D");
            }
        });

        btnHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodLoggingActivity(R.drawable.happy_icon, "Yay! :)");
            }
        });
        btnMeh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodLoggingActivity(R.drawable.meh_icon, "Meh :/");
            }
        });
        btnSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodLoggingActivity(R.drawable.sad_icon, "Awwww :(");
            }
        });
        btnUpset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoodLoggingActivity(R.drawable.upset_icon,"Oh no :(");
            }
        });

    }
    private void openMoodLoggingActivity(int iconResId, String moodText) {
        Intent intent = new Intent(MainActivity.this, MoodLogging.class);
        intent.putExtra("mood_icon", iconResId);
        intent.putExtra("mood_text", moodText);
        startActivity(intent);
    }

}
