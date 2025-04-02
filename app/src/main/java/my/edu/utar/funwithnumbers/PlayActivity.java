package my.edu.utar.funwithnumbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        getOnBackPressedDispatcher().addCallback(this, callback);

        Button btnCompare = findViewById(R.id.btnCompare);
        Button btnOrder = findViewById(R.id.btnOrder);
        Button btnCompose = findViewById(R.id.btnCompose);
        Button btnBack = findViewById(R.id.btnBack);

        btnCompare.setOnClickListener(v -> switchActivity(CompareActivity.class));
        btnOrder.setOnClickListener(v -> switchActivity(ChooseOrderActivity.class));
        btnCompose.setOnClickListener(v -> switchActivity(ComposeActivity.class));
        btnBack.setOnClickListener(v -> {
            isChangingActivity = true;
            Intent intent = new Intent(PlayActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close PlayActivity to prevent stacking
        });
    }

    private void switchActivity(Class<?> targetActivity) {
        isChangingActivity = true;
        Intent intent = new Intent(PlayActivity.this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
            Intent intent = new Intent(PlayActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    };
}