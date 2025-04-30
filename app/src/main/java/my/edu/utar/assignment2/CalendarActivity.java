package my.edu.utar.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar); // Make sure this matches your XML layout name

        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDay = eventDay.getCalendar();

                int year = clickedDay.get(Calendar.YEAR);
                int month = clickedDay.get(Calendar.MONTH) + 1; // Months are 0-based
                int day = clickedDay.get(Calendar.DAY_OF_MONTH);

                String selectedDate = String.format("%04d-%02d-%02d", year, month, day);

                // For now, just show a toast
                Toast.makeText(CalendarActivity.this, "Clicked: " + selectedDate, Toast.LENGTH_SHORT).show();

                // Later: Retrieve mood data from SharedPreferences using this date
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
