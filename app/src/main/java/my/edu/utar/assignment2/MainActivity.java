package my.edu.utar.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        String weatherDesc;

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


        //WeatherAPI
        TextView weatherTemp = findViewById(R.id.weatherTempText);
        ImageView weatherIcon = findViewById(R.id.weatherIcon);
        TextView weatherName = findViewById(R.id.weatherName);
        ImageButton btnMusic = findViewById(R.id.btnMusic);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeather("Kampar","35b04f55b9246cc3c767a61402f3f868","metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()){
                    WeatherResponse weather = response.body();
                    if (weather!=null && !weather.weather.isEmpty()){
                        int temp = (int) Math.round(weather.main.temp);
                        weatherTemp.setText(temp + "°C");
                        String description = weather.weather.get(0).description;
                        weatherName.setText(description);
                        String iconCode = weather.weather.get(0).icon;
                        String iconURL = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                        Glide.with(MainActivity.this).load(iconURL).into(weatherIcon);

                        btnMusic.setOnClickListener(view -> {

                            YesNoDialogBox.show(
                                    MainActivity.this,
                                    "Weather update: " + description,
                                    "Feeling Moody because of the weather?",
                                    "Mhm","Not really",
                                    yesView -> {
                                        String mainWeather = weather.weather.get(0).main;
                                        Intent intent = new Intent(MainActivity.this, GeneratingPlaylistActivity.class);
                                        intent.putExtra("EXTRA_WEATHER_MAIN",mainWeather);
                                        intent.putExtra("isWeatherPlaylist",true);
                                        startActivity(intent);
                                    },
                                    noView -> closeContextMenu()
                            );
                        });
                    }
                    else {
                        weatherName.setText("Failed to get weather.");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("Weather","Error: " + t.getMessage());
                weatherName.setText("Error loading weather.");
            }
        });

        btnExcited.setOnClickListener(v ->
                openMoodLoggingActivity(R.drawable.excited_icon, "Yippee! :D", "excited", weatherName.getText().toString())
        );
        btnHappy.setOnClickListener(v ->
                openMoodLoggingActivity(R.drawable.happy_icon, "Yay! :)", "happy", weatherName.getText().toString())
        );
        btnMeh.setOnClickListener(v ->
                openMoodLoggingActivity(R.drawable.meh_icon, "Meh :/", "meh", weatherName.getText().toString())
        );
        btnSad.setOnClickListener(v ->
                openMoodLoggingActivity(R.drawable.sad_icon, "Awwww :(", "sad", weatherName.getText().toString())
        );
        btnUpset.setOnClickListener(v ->
                openMoodLoggingActivity(R.drawable.upset_icon, "Oh no :(", "upset", weatherName.getText().toString())
        );

        ImageButton btnCalendar = findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }
    private void openMoodLoggingActivity(int iconResId, String moodText, String moodKey, String weatherDesc) {
        Intent intent = new Intent(MainActivity.this, MoodLogging.class);
        intent.putExtra("mood_icon", iconResId);
        intent.putExtra("mood_text", moodText);
        intent.putExtra("EXTRA_MOOD_KEY", moodKey);
        intent.putExtra("weather_desc", weatherDesc);
        startActivity(intent);
    }



}
