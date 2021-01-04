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

    TextView greetingText, branchName, branchDescription;
    ImageView earsLogo;

    Animation fadeIn;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        greetingText = (TextView) findViewById(R.id.welcome_greeting_text);
        branchName = (TextView) findViewById(R.id.welcome_branch_name);
        branchDescription = (TextView) findViewById(R.id.welcome_branch_description);
        earsLogo = (ImageView) findViewById(R.id.welcome_ears_logo);

        fadeIn = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                greetingText.setText("Hi " + getIntent().getStringExtra("USER_NAME") + "!");
                greetingText.setAnimation(fadeIn);

                firedb.getReference().child("branches").child(getIntent().getStringExtra("BRANCH_KEY")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snapshot) {

                        branchName.setText(snapshot.child("name").getValue().toString());
                        branchDescription.setText(snapshot.child("description").getValue().toString());

                        final Branch branch = new Branch(
                                snapshot.child("key").getValue().toString(),
                                snapshot.child("name").getValue().toString(),
                                snapshot.child("description").getValue().toString()
                        );

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(WelcomeActivity.this, ScanActivity.class);
                                intent.putExtra("BRANCH", branch);

                                Pair[] pairs = new Pair[3];
                                pairs[0] = new Pair<View, String>(earsLogo, "ears_logo");
                                pairs[1] = new Pair<View, String>(branchName, "branch_name");
                                pairs[2] = new Pair<View, String>(branchDescription, "branch_description");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, pairs);
                                startActivity(intent, options.toBundle());
                            }
                        }, 4000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(WelcomeActivity.this, "Something went wrong. Please restart your app.", Toast.LENGTH_LONG).show();
                    }
                });


            }
        },1000);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(WelcomeActivity.this, "Press back again to do nothing.", Toast.LENGTH_LONG).show();
    }
}