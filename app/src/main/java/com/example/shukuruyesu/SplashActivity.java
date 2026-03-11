package com.example.shukuruyesu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        TextView english = findViewById(R.id.textEnglish);
        TextView arabic = findViewById(R.id.textArabic);

        Animation englishAnim = AnimationUtils.loadAnimation(this, R.anim.anim);
        Animation arabicAnim = AnimationUtils.loadAnimation(this, R.anim.anim);

        // English first
        english.startAnimation(englishAnim);
        english.setAlpha(1f);

        // Arabic after 800ms (after English animation)
        new Handler().postDelayed(() -> {
            arabic.startAnimation(arabicAnim);
            arabic.setAlpha(1f);
        }, 800);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 5500); // 5.5 seconds delay
    }
}