package my.edu.utar.funwithnumbers;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class CompareActivity extends AppCompatActivity {
    private TextView txtNum1, txtNum2, txtScore, txtAttempts;
    private Button btnLess, btnGreater, btnReset, btnQuit;
    private ImageView imgResult;
    private int num1, num2, score = 0, attempts = 3;
    private Random random = new Random();
    private Handler handler = new Handler();
    private boolean isChangingActivity = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        getOnBackPressedDispatcher().addCallback(this, callback);

        txtNum1 = findViewById(R.id.txtNum1);
        txtNum2 = findViewById(R.id.txtNum2);
        txtScore = findViewById(R.id.txtScore);
        txtAttempts = findViewById(R.id.txtAttempts);
        btnLess = findViewById(R.id.btnLess);
        btnGreater = findViewById(R.id.btnGreater);
        btnReset = findViewById(R.id.btnReset);
        imgResult = findViewById(R.id.imgResult);
        btnQuit = findViewById(R.id.btnQuit);

        generateNumbers();

        btnLess.setOnClickListener(v -> checkAnswer("<"));
        btnGreater.setOnClickListener(v -> checkAnswer(">"));
        btnReset.setOnClickListener(v -> resetGame());
        btnQuit.setOnClickListener(v -> quitGame());
    }

    private void generateNumbers() {
        num1 = random.nextInt(1000); // Generates a number between 0-999
        num2 = random.nextInt(1000);
        while (num1 == num2) num2 = random.nextInt(1000); // Ensure numbers are not the same

        txtNum1.setText(String.valueOf(num1));
        txtNum2.setText(String.valueOf(num2));
        txtScore.setText("Score: " + score);
        txtAttempts.setText("Attempts left: " + attempts);
        imgResult.setImageDrawable(null);

        // Reset the comparison symbol to "?"
        TextView comparisonSymbol = findViewById(R.id.txtComparison);
        comparisonSymbol.setText("?");
        enableButtons();
    }

    private void checkAnswer(String operator) {
        boolean isCorrect = (operator.equals(">") && num1 > num2) || (operator.equals("<") && num1 < num2);

        // Update the middle "?" to the selected operator
        TextView comparisonSymbol = findViewById(R.id.txtComparison);
        if (operator.equals(">")) {
            comparisonSymbol.setText("greater than");
        } else if (operator.equals("<")) {
            comparisonSymbol.setText("less than");
        }

        if (isCorrect) {
            score++;
            imgResult.setImageResource(R.drawable.correct_icon); // Show correct icon
            disableButtons();
        } else {
            attempts--;
            imgResult.setImageResource(R.drawable.wrong_icon); // Show wrong icon
            disableButtons();
        }

        txtScore.setText("Score: " + score);
        txtAttempts.setText("Attempts left: " + attempts);

        handler.postDelayed(() -> {
            if (attempts == 0) {
                goToGameOver();
            } else {
                generateNumbers();
            }
        }, 1000); // 1-second delay before next question
    }

    private void goToGameOver() {
        isChangingActivity = true;
        Intent intent = new Intent(CompareActivity.this, GameOverActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("GAME_MODE", "COMPARE"); // Pass correct game mode
        startActivity(intent);
        finish();
    }
    private void resetGame() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Reset")
                .setMessage("Are you sure you want to reset the game?")
                .setPositiveButton("Yes",
                        (dialog, which) -> {
                            // Reset the game only if the user confirms
                            score = 0;
                            attempts = 3;
                            generateNumbers();
                        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Just close the dialog
                .show();
    }

    private void quitGame() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Quit")
                .setMessage("Are you sure you want to quit the game?")
                .setPositiveButton("Yes", (dialog, which) -> { goToGameOver(); })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Just close the dialog
                .show();
    }

    private void disableButtons() {
        btnGreater.setEnabled(false);
        btnLess.setEnabled(false);
    }

    private void enableButtons() {
        btnGreater.setEnabled(true);
        btnLess.setEnabled(true);
    }

    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            quitGame();
        }
    };

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