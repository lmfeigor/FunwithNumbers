package my.edu.utar.funwithnumbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseOrderActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_order);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        getOnBackPressedDispatcher().addCallback(this, callback);

        Button btnAscending = findViewById(R.id.btnAscending);
        Button btnDescending = findViewById(R.id.btnDescending);
        Button btnBack = findViewById(R.id.btnBack);

        btnAscending.setOnClickListener(v -> startGame(true)); // Ascending order
        btnDescending.setOnClickListener(v -> startGame(false)); // Descending order
        btnBack.setOnClickListener(v -> {
            isChangingActivity = true;
            Intent intent = new Intent(ChooseOrderActivity.this, PlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void startGame(boolean isAscending) {
        isChangingActivity = true;
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("ORDER_MODE", isAscending);
        intent.putExtra("GAME_MODE", "ORDER"); // Store order mode
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

    private final OnBackPressedCallback callback = new OnBackPressedCallback(
            true) {
        @Override
        public void handleOnBackPressed() {
            isChangingActivity = true;
            Intent intent = new Intent(ChooseOrderActivity.this, PlayActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    };
}