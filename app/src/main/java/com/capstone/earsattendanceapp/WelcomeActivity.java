package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    TextView greetingText;
    ImageView earsLogo;

    Animation fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        greetingText = (TextView) findViewById(R.id.welcome_greeting_text);
        earsLogo = (ImageView) findViewById(R.id.welcome_ears_logo);

        fadeIn = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                greetingText.setText("Hi " + getIntent().getStringExtra("USER_NAME") + "!");
                greetingText.setAnimation(fadeIn);
            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, BranchesActivity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(earsLogo, "ears_logo");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        },4000);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(WelcomeActivity.this, "Press back again to do nothing.", Toast.LENGTH_LONG).show();
    }
}