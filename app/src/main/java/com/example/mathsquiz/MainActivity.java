package com.example.mathsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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

        // Delay before newActivity():
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();


    }
}