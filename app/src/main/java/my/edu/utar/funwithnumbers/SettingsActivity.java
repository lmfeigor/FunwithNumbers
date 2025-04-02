package my.edu.utar.funwithnumbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private Switch musicSwitch;
    private SeekBar volumeSeekBar;
    private SharedPreferences sharedPreferences;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        int savedVolume = sharedPreferences.getInt("MusicVolume", 100); // Default 100%
        getOnBackPressedDispatcher().addCallback(this, callback);

        musicSwitch = findViewById(R.id.musicSwitch);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        Button btnBack = findViewById(R.id.btnBack);

        // Set switch state
        musicSwitch.setChecked(isMusicOn);

        // Restore saved volume
        volumeSeekBar.setMax(100);
        volumeSeekBar.setProgress(savedVolume);
        MusicManager.setVolume(savedVolume / 100f); // Apply saved volume

        // Handle switch toggle
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("MusicEnabled", isChecked);
            editor.apply();

            if (isChecked) {
                MusicManager.startMusic(this);
            } else {
                MusicManager.stopMusic();
            }

            Toast.makeText(this, "Music " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        // Handle volume change
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MusicManager.setVolume(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("MusicVolume", volumeSeekBar.getProgress()); // Save volume
                editor.apply();

                Toast.makeText(SettingsActivity.this, "Volume Set", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button - return to MainActivity
        btnBack.setOnClickListener(v -> {
            isChangingActivity = true;
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        if (isMusicOn) {
            MusicManager.startMusic(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isChangingActivity) {
            MusicManager.stopMusic();
        }
    }

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            isChangingActivity = true;
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close SettingsActivity to prevent stacking
        }
    };
}
