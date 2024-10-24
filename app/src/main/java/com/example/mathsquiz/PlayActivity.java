package com.example.mathsquiz;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import static java.lang.Integer.parseInt;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    TextView score, timer, num1, num2, operator, message;
    EditText input;
    Button submitBtn, skipBtn;
    ImageView life1, life2, life3;
    int chance = 3;
    int correctAns;

    CountDownTimer countDownTimer;
    private static final long START_TIMER_IN_MILLIS = 20000; // 1000ms = 1s
    Boolean timer_running;
    long TIME_LEFT_IN_MILLIS = START_TIMER_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Fetch padding defined in XML
            int currentPaddingLeft = v.getPaddingLeft();
            int currentPaddingTop = v.getPaddingTop();
            int currentPaddingRight = v.getPaddingRight();
            int currentPaddingBottom = v.getPaddingBottom();

            // Adjust padding by adding system insets to the XML-defined padding
            v.setPadding(
                    currentPaddingLeft + systemBars.left, // Add inset to the existing left padding
                    systemBars.top,   // Add inset to the existing top padding
                    currentPaddingRight + systemBars.right, // Add inset to the existing right padding
                    systemBars.bottom // Add inset to the existing bottom padding
            );

            return insets;
        });

        score = findViewById(R.id.score);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        operator = findViewById(R.id.operator);
        input = findViewById(R.id.input);
        submitBtn = findViewById(R.id.submitBtn);
        skipBtn = findViewById(R.id.skipBtn);
        life1 = findViewById(R.id.life1);
        life2 = findViewById(R.id.life2);
        life3 = findViewById(R.id.life3);
        message = findViewById(R.id.message);
        timer = findViewById(R.id.timer);

        newQuestion();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();

            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                resetTimer();

                newQuestion();
                newActivity();
            }
        });

    }

    public void newQuestion(){

        if (chance == 0) {
            return;
        }

        input.setText("");
        message.setText("");


        Random rand = new Random();
        int randNum1 = rand.nextInt(201) - 100;
        int randNum2 = rand.nextInt(201) - 100;

        char[] operatorArr = {'+', '-', '*', '/', '%'};

        int randOperator = rand.nextInt(operatorArr.length);

        num1.setText(String.valueOf(randNum1));

        if (randNum2 < 0){
            num2.setText("(" + randNum2 + ")");
        }
        else{
            num2.setText(String.valueOf(randNum2));
        }
        operator.setText(String.valueOf(operatorArr[randOperator]));

        switch (operatorArr[randOperator]) {
            case '+':
                correctAns = randNum1 + randNum2;
                break;

            case '-':
                correctAns = randNum1 - randNum2;
                break;

            case '*':
                correctAns = randNum1 * randNum2;
                break;

            case '/':
                correctAns = (randNum2 != 0) ? (randNum1 / randNum2) : 0;  // Prevent division by zero
                break;

            case '%':
                correctAns = (randNum2 != 0) ? (randNum1 % randNum2) : 0;  // Prevent division by zero
                break;
        }

        resetTimer();
        startTimer();

    }

    public void checkInput(){

        if (input.getText().toString().equals("")){
            message.setText("Please enter your answer!");
            return;
        }

        if (correctAns == (parseInt(input.getText().toString()))){
            pauseTimer();
            score.setText(String.valueOf(parseInt(score.getText().toString()) + 1));
            message.setText("Correct!");

            // Delay for 1 seconds before next question
            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    message.setText("");
                    input.setText("");
                    resetTimer();
                    newQuestion();
                }
            }.start();
        }

        else {
            updateLife();
            message.setText("Try again!");

            // Delay for 1 seconds before next question
            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    message.setText("");
                    input.setText("");
                    newActivity();
                }
            }.start();
        }
    }

    public void newActivity(){
        if (chance == 0){
            pauseTimer();
            input.setText(String.valueOf(correctAns));
            message.setText("You're out of lives,\nbetter luck next time!");

            // Delay before newActivity():
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(PlayActivity.this, GameOverActivity.class);
                    intent.putExtra("totalScore", parseInt(score.getText().toString()));
                    startActivity(intent);
                    finish();
                }
            }.start();

        }
    }

    public void updateLife(){
        if (chance == 3) life3.setVisibility(View.INVISIBLE);

        else if (chance == 2) life2.setVisibility(View.INVISIBLE);

        else if (chance == 1) {
            life1.setVisibility(View.INVISIBLE);
            submitBtn.setEnabled(false);
        }

        chance -= 1;
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(TIME_LEFT_IN_MILLIS, 1000) {
            @Override
            public void onTick(long l) {
                TIME_LEFT_IN_MILLIS = l;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timer_running = false;
                pauseTimer();
                resetTimer();
                updateTimerText();
                updateLife();
                message.setText("Time up!");
                input.setText(String.valueOf(correctAns));

                // Delay for 1 seconds before next question
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        newActivity();
                        newQuestion();
                    }
                }.start();

            }
        }.start();
        timer_running = true;
    }

    public void updateTimerText() {
        int seconds = (int) (TIME_LEFT_IN_MILLIS / 1000) % 60;
        String time_left = String.format(Locale.getDefault(), "%02d", seconds);
        timer.setText(time_left + " sec");
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        timer_running = false;
    }

    public void resetTimer() {
        TIME_LEFT_IN_MILLIS = START_TIMER_IN_MILLIS;
        updateTimerText();
    }
}
