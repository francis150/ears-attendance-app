package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    ImageView earsLogo;

    FirebaseAuth fireauth = FirebaseAuth.getInstance();
    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        earsLogo = (ImageView) findViewById(R.id.splash_ears_logo);

        final Handler delay = new Handler(Looper.getMainLooper());
        delay.postDelayed(new Runnable() {
            public void run() {

                FirebaseUser currentUser = fireauth.getCurrentUser();

                if (currentUser != null) {

                    firedb.getReference().child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            intent.putExtra("USER_NAME", dataSnapshot.child("fname").getValue().toString().split(" ")[0]);
                            intent.putExtra("BRANCH_KEY", dataSnapshot.child("permissions/attendance_app_branch").getValue().toString());
                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(earsLogo, "ears_logo");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                            startActivity(intent, options.toBundle());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            intent.putExtra("USER_NAME", "there");
                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(earsLogo, "ears_logo");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                            startActivity(intent, options.toBundle());
                        }
                    });

                } else {

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(earsLogo, "ears_logo");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                    startActivity(intent, options.toBundle());

                }

            }
        }, 1000);
    }
}
