package my.edu.utar.funwithnumbers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    private String lastGameMode;
    private boolean lastOrderMode; // true = Ascending, false = Descending
    private SharedPreferences sharedPreferences;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);

        TextView txtFinalScore = findViewById(R.id.txtFinalScore);
        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnMainMenu = findViewById(R.id.btnMainMenu);

        // Get the final score
        int finalScore = getIntent().getIntExtra("SCORE", 0);
        txtFinalScore.setText("Your Score: " + finalScore);

        // Get the last played game mode
        lastGameMode = getIntent().getStringExtra("GAME_MODE");
        lastOrderMode = getIntent().getBooleanExtra("ORDER_MODE", true);

        btnRestart.setOnClickListener(v -> restartGame());

        btnMainMenu.setOnClickListener(v -> goToMainMenu());

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToMainMenu();
            }
        });
    }

    private void restartGame() {
        Intent intent;
        switch (lastGameMode) {
            case "ORDER":
                isChangingActivity = true;
                intent = new Intent(GameOverActivity.this, OrderActivity.class);
                intent.putExtra("ORDER_MODE", lastOrderMode);
                intent.putExtra("GAME_MODE", "ORDER");
                break;
            case "COMPARE":
                isChangingActivity = true;
                intent = new Intent(GameOverActivity.this, CompareActivity.class);
                intent.putExtra("GAME_MODE", "COMPARE");
                break;
            case "COMPOSE":
                isChangingActivity = true;
                intent = new Intent(GameOverActivity.this, ComposeActivity.class);
                intent.putExtra("GAME_MODE", "COMPOSE");
                break;
            default:
                intent = new Intent(GameOverActivity.this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void goToMainMenu() {
        isChangingActivity = true;
        Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
}
