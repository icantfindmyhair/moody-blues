package my.edu.utar.assignment2;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView dateTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTimeText = findViewById(R.id.dateTimeText);

        // Set current date and time
        String currentDateTime = new SimpleDateFormat("EEEE, MMM d yyyy\nhh:mm a", Locale.getDefault()).format(new Date());
        dateTimeText.setText(currentDateTime);
    }
}
