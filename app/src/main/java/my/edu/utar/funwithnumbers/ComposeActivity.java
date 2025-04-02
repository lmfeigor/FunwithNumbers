package my.edu.utar.funwithnumbers;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class ComposeActivity extends AppCompatActivity {
    private TextView txtScore, txtAttempts, box1, box2, targetNumber, operator;
    private GridLayout numberGrid;
    private ImageView imgResult;
    private Button btnCheckAnswer, btnReset, btnQuit;
    private ArrayList<Integer> numbers = new ArrayList<>();
    private ArrayList<Integer> selectedNumbers = new ArrayList<>();
    private Random random = new Random();
    private Handler handler = new Handler();
    private int score = 0, attempts = 3;
    private Button selectedButton1 = null, selectedButton2 = null;
    private boolean isChangingActivity = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        getOnBackPressedDispatcher().addCallback(this, callback);

        txtScore = findViewById(R.id.txtScore);
        txtAttempts = findViewById(R.id.txtAttempts);
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        targetNumber = findViewById(R.id.targetNumber);
        operator = findViewById(R.id.operator);
        numberGrid = findViewById(R.id.numberGrid);
        imgResult = findViewById(R.id.imgResult);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnReset = findViewById(R.id.btnReset);
        btnQuit = findViewById(R.id.btnQuit);

        setupWhiteBoxClickListeners();
        generateNewEquation();

        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnReset.setOnClickListener(v -> resetGame());
        btnQuit.setOnClickListener(v -> quitGame());
    }

    private void generateNewEquation() {
        numbers.clear();
        selectedNumbers.clear();
        numberGrid.removeAllViews();
        imgResult.setVisibility(View.INVISIBLE);
        box1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        box2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        box1.setText("");
        box2.setText("");
        selectedButton1 = null;
        selectedButton2 = null;

        // Generate num1 and num2 such that their sum is also in range 0-999
        int num1, num2, sum;
        do {
            num1 = random.nextInt(1000);  // Get a number between 0-999
            num2 = random.nextInt(1000);  // Get another number between 0-999
            sum = num1 + num2;            // Calculate sum
        } while (sum > 999); // Repeat until sum is also <= 999

        operator.setText("and");
        targetNumber.setText(String.valueOf(sum)); // Set the target

        numbers.add(num1);
        numbers.add(num2);

        // Generate two incorrect numbers (between 0-999, but not equal to sum)
        while (numbers.size() < 4) {
            int fakeNum = random.nextInt(1000);
            if (fakeNum != sum && !numbers.contains(fakeNum)) { // Avoid duplicates
                numbers.add(fakeNum);
            }
        }

        // **Shuffle the numbers so the answer is not always in the first two buttons**
        Collections.shuffle(numbers);

        for (int num : numbers) {
            Button numberButton = new Button(this);
            numberButton.setText(String.valueOf(num));
            numberButton.setTextSize(24);
            numberButton.setBackgroundResource(R.drawable.game_buttons);
            numberButton.setTextColor(Color.BLACK);
            numberButton.setPadding(40, 20, 40, 20);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(20, 20, 20, 20);
            numberButton.setLayoutParams(params);

            numberButton.setOnClickListener(v -> selectNumber(numberButton));
            numberGrid.addView(numberButton);

            enableButtons();
        }
        updateScoreAndAttempts();
    }

    private void selectNumber(Button numberButton) {
        int selectedNum = Integer.parseInt(numberButton.getText().toString());

        if (selectedButton1 == null) {
            selectedButton1 = numberButton;
            box1.setText(String.valueOf(selectedNum));
            box1.setBackgroundResource(R.drawable.game_buttons);
        } else if (selectedButton2 == null) {
            selectedButton2 = numberButton;
            box2.setText(String.valueOf(selectedNum));
            box2.setBackgroundResource(R.drawable.game_buttons);
        } else
            return;

        numberButton.setEnabled(false);
        numberButton.setBackgroundColor(Color.DKGRAY);
    }

    private void setupWhiteBoxClickListeners() {
        box1.setOnClickListener(v -> deselectNumber(1));
        box2.setOnClickListener(v -> deselectNumber(2));
    }

    private void deselectNumber(int boxNumber) {
        if (boxNumber == 1 && selectedButton1 != null) {
            selectedButton1.setEnabled(true);
            selectedButton1.setBackgroundResource(R.drawable.game_buttons);
            box1.setText("");
            box1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            selectedButton1 = null;
        } else if (boxNumber == 2 && selectedButton2 != null) {
            selectedButton2.setEnabled(true);
            selectedButton2.setBackgroundResource(R.drawable.game_buttons);
            box2.setText("");
            box2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            selectedButton2 = null;
        }
    }

    private void checkAnswer() {
        if (selectedButton1 == null || selectedButton2 == null) {
            Toast.makeText(this, "Select two numbers!", Toast.LENGTH_SHORT).show();
            return;
        }

        int num1 = Integer.parseInt(selectedButton1.getText().toString());
        int num2 = Integer.parseInt(selectedButton2.getText().toString());
        int expectedResult = Integer.parseInt(targetNumber.getText().toString());

        int calculatedResult = num1 + num2;
        boolean isCorrect = calculatedResult == expectedResult;

        if (isCorrect) {
            imgResult.setImageResource(R.drawable.correct_icon); // Correct icon
            score++;
            disableButtons();
        } else {
            imgResult.setImageResource(R.drawable.wrong_icon); // Wrong icon
            attempts--;
            disableButtons();
        }

        imgResult.setVisibility(View.VISIBLE);
        updateScoreAndAttempts();

        handler.postDelayed(() -> {
            if (attempts == 0) {
                goToGameOver();
            } else {
                generateNewEquation();
            }
        }, 1000);
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
                            generateNewEquation();
                        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Just close the dialog
                .show();
    }

    private void updateScoreAndAttempts() {
        txtScore.setText("Score: " + score);
        txtAttempts.setText("Attempts left: " + attempts);
    }

    private void goToGameOver() {
        isChangingActivity = true;
        Intent intent = new Intent(ComposeActivity.this, GameOverActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("GAME_MODE", "COMPOSE"); // Pass correct game mode
        startActivity(intent);
        finish();
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
        btnCheckAnswer.setEnabled(false);
        box1.setEnabled(false);
        box2.setEnabled(false);
    }

    private void enableButtons() {
        btnCheckAnswer.setEnabled(true);
        box1.setEnabled(true);
        box2.setEnabled(true);
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
