package my.edu.utar.assignment2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class GeneratingPlaylistActivity extends AppCompatActivity {
    private static final String TAG          = "GenGroupPlaylist";
    private static final String CLIENT_ID    = "86c747e18d4a45628d26ffd6483c0ce6";
    private static final String REDIRECT_URI = "moodyblues://callback";

    private String codeVerifier;
    private String moodKey;
    private OkHttpClient httpClient;
    private Random random = new Random();

    //weather
    private String weatherMain;
    private boolean isWeatherPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating_playlist);

        Button backButton = findViewById(R.id.cancelButton);
        backButton.setOnClickListener(v -> finish());

        moodKey = getIntent().getStringExtra("EXTRA_MOOD_KEY");
        //weather
        weatherMain = getIntent().getStringExtra("EXTRA_WEATHER_MAIN");
        isWeatherPlaylist = getIntent().getBooleanExtra("isWeatherPlaylist", false);

        //Log.d(TAG, "Generating playlist for: " + moodKey);
        Log.d(TAG, "Generating playlist for: " +
                (isWeatherPlaylist ? "Weather: " + weatherMain : "Mood: " + moodKey));


        HttpLoggingInterceptor logg = new HttpLoggingInterceptor(msg -> Log.d("HTTP", msg));
        logg.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder().addInterceptor(logg).build();

        codeVerifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(codeVerifier);

        HttpUrl authUrl = HttpUrl.parse("https://accounts.spotify.com/authorize").newBuilder()
                .addQueryParameter("response_type", "code")
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("redirect_uri", REDIRECT_URI)
                .addQueryParameter("code_challenge_method", "S256")
                .addQueryParameter("code_challenge", challenge)
                .addQueryParameter("scope", "playlist-read-private")
                .build();
        new CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(authUrl.toString()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            exchangeCodeForToken(code);
        } else if (uri != null && uri.getQueryParameter("error") != null) {
            Toast.makeText(this, "Auth error: " + uri.getQueryParameter("error"), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void exchangeCodeForToken(String code) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                okhttp3.RequestBody body = new okhttp3.FormBody.Builder()
                        .add("grant_type", "authorization_code")
                        .add("code", code)
                        .add("redirect_uri", REDIRECT_URI)
                        .add("client_id", CLIENT_ID)
                        .add("code_verifier", codeVerifier)
                        .build();
                Request req = new Request.Builder()
                        .url("https://accounts.spotify.com/api/token")
                        .post(body)
                        .build();
                try (Response resp = httpClient.newCall(req).execute()) {
                    if (!resp.isSuccessful()) throw new IOException("Token exchange failed: " + resp);
                    JSONObject json = new JSONObject(resp.body().string());
                    String token = json.getString("access_token");
                    searchAndLaunch(token, moodKey);
                }
            } catch (Exception e) {
                Log.e(TAG, "Token exchange failed", e);
                runOnUiThread(() -> Toast.makeText(this, "Authorization failed.", Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }

    /**
     * Search Spotify playlists by randomly-chosen mood keywords and launch one.
     */
    private void searchAndLaunch(String token, String mood) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Pick a random keyword
                //List<String> keys = getKeywords(mood);

                //make change for weather
                List<String> keys;
                if(isWeatherPlaylist){
                    keys = getWeatherKeywords(weatherMain);
                }
                else {
                    keys = getKeywords(mood);
                }

                String kw = keys.get(random.nextInt(keys.size()));
                Log.d(TAG, "Searching playlists with keyword: " + kw);

                HttpUrl url = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder()
                        .addQueryParameter("q", kw)
                        .addQueryParameter("type", "playlist")
                        .addQueryParameter("limit", "5")
                        .build();
                Request r = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                List<String> ids = new ArrayList<>();
                try (Response res = httpClient.newCall(r).execute()) {
                    if (!res.isSuccessful()) throw new IOException("Search failed: " + res);
                    JSONObject root = new JSONObject(res.body().string());
                    JSONObject playlists = root.optJSONObject("playlists");
                    if (playlists != null) {
                        JSONArray items = playlists.optJSONArray("items");
                        if (items != null) {
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.optJSONObject(i);
                                if (item != null) {
                                    String id = item.optString("id", null);
                                    if (id != null) ids.add(id);
                                }
                            }
                        }
                    }
                }

                if (ids.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "No playlist found for mood: " + mood, Toast.LENGTH_LONG).show());
                    finish();
                    return;
                }

                String chosen = ids.get(random.nextInt(ids.size()));
                Intent launch = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:playlist:" + chosen));
                runOnUiThread(() -> {
                    if (launch.resolveActivity(getPackageManager()) != null) {
                        startActivity(launch);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://open.spotify.com/playlist/" + chosen)));
                    }
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "Search/launch failed", e);
                runOnUiThread(() -> Toast.makeText(this,
                        "Failed to find playlist.", Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }

    private List<String> getKeywords(String mood) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("excited", Arrays.asList("exciting","electronic","hype","adrenaline","energetic","upbeat","party"));
        map.put("happy",   Arrays.asList("happy","uplifting","feel-good","joyful","bright","cheerful","sunshine"));
        map.put("meh",     Arrays.asList("chill","vibes","lo-fi","mellow","relaxing","downtempo","laid-back"));
        map.put("sad",     Arrays.asList("sad","melancholy","acoustic","soulful","heartbreak","emotional","blues"));
        map.put("upset",   Arrays.asList("angry","intense","rock","aggressive","punk","rebellion","revenge"));
        return map.getOrDefault(mood, Arrays.asList(mood));
    }

    //weather
    private List<String> getWeatherKeywords(String weatherMain){
        switch (weatherMain.toLowerCase()){
            case "thunderstorm":
                return Arrays.asList("storm","epic","dramatic","intense");
            case "drizzle":
            case "rain":
                return Arrays.asList("rainy","cozy","jazz","piano");
            case "snow":
                return Arrays.asList("winter","christmas","holiday","snow");
            case "clear":
                return Arrays.asList("sunny","happy","summer","uplifting");
            case "clouds":
                return Arrays.asList("cloudy","chill","lo-fi","ambient");
            case "mist":
            case "smoke":
            case "haze":
            case "dust":
            case "fog":
            case "sand":
            case "ash":
            case "squall":
            case "tornado":
                return Arrays.asList("mysterious","ambient","ethereal");
            default:
                return Arrays.asList("weather","mood","chill");
        }
    }

    private String generateCodeVerifier() {
        byte[] rnd = new byte[32]; new SecureRandom().nextBytes(rnd);
        return Base64.encodeToString(rnd, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    private String generateCodeChallenge(String v) {
        try {
            byte[] ba = v.getBytes("US-ASCII");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dg = md.digest(ba);
            return Base64.encodeToString(dg, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
