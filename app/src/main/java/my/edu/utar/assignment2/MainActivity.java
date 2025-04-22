package my.edu.utar.assignment2;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

}
