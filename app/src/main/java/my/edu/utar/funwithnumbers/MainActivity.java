package my.edu.utar.funwithnumbers;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        float savedVolume = sharedPreferences.getInt("MusicVolume", 100) / 100f; // Get saved volume
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Set saved music volume before starting music
        MusicManager.setVolume(savedVolume);

        if (isMusicOn) {
            MusicManager.startMusic(this);
        }

        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnExit = findViewById(R.id.btnExit);

        btnPlay.setOnClickListener(v -> switchActivity(PlayActivity.class));
        btnSettings.setOnClickListener(v -> switchActivity(SettingsActivity.class));
        btnExit.setOnClickListener(v -> showExitConfirmation());
    }

    private void switchActivity(Class<?> targetActivity) {
        isChangingActivity = true;
        Intent intent = new Intent(MainActivity.this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes",
                        (dialog, which) -> {
                            MusicManager.releaseMusic(); // Stop music when exiting app
                            finish();
                        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isChangingActivity) {
            MusicManager.stopMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChangingActivity = false;

        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        float savedVolume = sharedPreferences.getInt("MusicVolume", 100) / 100f;

        MusicManager.setVolume(savedVolume);

        if (isMusicOn) {
            MusicManager.startMusic(this);
        }
    }

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            showExitConfirmation();
        }
    };
}