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
import java.util.Collections;
import java.util.Random;

public class OrderActivity extends AppCompatActivity {
    private TextView txtScore, txtAttempts, box1, box2, box3, box4;
    private GridLayout numberGrid;
    private ImageView imgResult;
    private Button btnCheckAnswer, btnReset, btnQuit;
    private ArrayList<Integer> numbers = new ArrayList<>();
    private ArrayList<Integer> selectedNumbers = new ArrayList<>();
    private Random random = new Random();
    private Handler handler = new Handler();
    private int score = 0, attempts = 3;
    private boolean isAscending; // To track order mode
    private Button selectedButton1, selectedButton2, selectedButton3, selectedButton4;
    private boolean isChangingActivity = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean("MusicEnabled", true);
        getOnBackPressedDispatcher().addCallback(this, callback);

        txtScore = findViewById(R.id.txtScore);
        txtAttempts = findViewById(R.id.txtAttempts);
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box3 = findViewById(R.id.box3);
        box4 = findViewById(R.id.box4);
        numberGrid = findViewById(R.id.numberGrid);
        imgResult = findViewById(R.id.imgResult);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnReset = findViewById(R.id.btnReset);
        btnQuit = findViewById(R.id.btnQuit);

        isAscending = getIntent().getBooleanExtra("ORDER_MODE", true); // Get order mode from ChooseOrderActivity

        setupWhiteBoxClickListeners();
        generateNewNumbers();

        btnCheckAnswer.setOnClickListener(v -> checkAnswer());
        btnReset.setOnClickListener(v -> resetGame());
        btnQuit.setOnClickListener(v -> quitGame());

        // Change Background Color According to Order Mode
        changeBackgroundColor();
    }

    private void generateNewNumbers() {
        numbers.clear();
        selectedNumbers.clear();
        numberGrid.removeAllViews();
        imgResult.setVisibility(View.INVISIBLE);
        box1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        box2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        box3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        box4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        box1.setText("");
        box2.setText("");
        box3.setText("");
        box4.setText("");
        selectedButton1 = null;
        selectedButton2 = null;
        selectedButton3 = null;
        selectedButton4 = null;

        // Generate 4 random numbers from 0 to 999
        for (int i = 0; i < 4; i++) {
            numbers.add(random.nextInt(1000)); // Range 0 to 999
        }

        // Display the numbers in buttons
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
        } else if (selectedButton3 == null) {
            selectedButton3 = numberButton;
            box3.setText(String.valueOf(selectedNum));
            box3.setBackgroundResource(R.drawable.game_buttons);
        } else if (selectedButton4 == null) {
            selectedButton4 = numberButton;
            box4.setText(String.valueOf(selectedNum));
            box4.setBackgroundResource(R.drawable.game_buttons);
        } else
            return;

        selectedNumbers.add(selectedNum);
        numberButton.setEnabled(false);
        numberButton.setBackgroundColor(Color.DKGRAY);
    }

    private void setupWhiteBoxClickListeners() {
        box1.setOnClickListener(v -> deselectNumber(1));
        box2.setOnClickListener(v -> deselectNumber(2));
        box3.setOnClickListener(v -> deselectNumber(3));
        box4.setOnClickListener(v -> deselectNumber(4));
    }

    private void deselectNumber(int boxNumber) {
        if (boxNumber == 1 && selectedButton1 != null) {
            selectedButton1.setEnabled(true);
            selectedButton1.setBackgroundResource(R.drawable.game_buttons);
            box1.setText("");
            box1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            selectedNumbers.remove(Integer.valueOf(selectedButton1.getText().toString()));
            selectedButton1 = null;
        } else if (boxNumber == 2 && selectedButton2 != null) {
            selectedButton2.setEnabled(true);
            selectedButton2.setBackgroundResource(R.drawable.game_buttons);
            box2.setText("");
            box2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            selectedNumbers.remove(Integer.valueOf(selectedButton2.getText().toString()));
            selectedButton2 = null;
        } else if (boxNumber == 3 && selectedButton3 != null) {
            selectedButton3.setEnabled(true);
            selectedButton3.setBackgroundResource(R.drawable.game_buttons);
            box3.setText("");
            box3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            selectedNumbers.remove(Integer.valueOf(selectedButton3.getText().toString()));
            selectedButton3 = null;
        } else if (boxNumber == 4 && selectedButton4 != null) {
            selectedButton4.setEnabled(true);
            selectedButton4.setBackgroundResource(R.drawable.game_buttons);
            box4.setText("");
            box4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            selectedNumbers.remove(Integer.valueOf(selectedButton4.getText().toString()));
            selectedButton4 = null;
        }
    }

    private void checkAnswer() {
        if (selectedNumbers.size() != 4) {
            Toast.makeText(this, "Fill up the slots!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Integer> sortedNumbers = new ArrayList<>(selectedNumbers);
        if (isAscending) {
            Collections.sort(sortedNumbers);
        } else {
            sortedNumbers.sort(Collections.reverseOrder());
        }

        boolean isCorrect = sortedNumbers.equals(selectedNumbers);

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
                generateNewNumbers();
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
                            generateNewNumbers();
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
        Intent intent = new Intent(OrderActivity.this, GameOverActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("GAME_MODE", "ORDER");
        intent.putExtra("ORDER_MODE", isAscending);
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
        box3.setEnabled(false);
        box4.setEnabled(false);
    }

    private void enableButtons() {
        btnCheckAnswer.setEnabled(true);
        box1.setEnabled(true);
        box2.setEnabled(true);
        box3.setEnabled(true);
        box4.setEnabled(true);
    }

    private void changeBackgroundColor() {
        View rootView = findViewById(android.R.id.content);

        // Set background color based on order mode
        if (isAscending) {
            rootView.setBackgroundColor(Color.parseColor("#CCCAF0"));
        } else {
            rootView.setBackgroundColor(Color.parseColor("#F9ECD9"));
        }
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
