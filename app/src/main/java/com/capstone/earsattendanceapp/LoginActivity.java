package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView txtIncorrectLogin;
    EditText txtUsername, txtPassword;
    Button btnLogin;
    ProgressBar progressBar;
    ImageView earsLogo;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();
    FirebaseAuth fireauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtIncorrectLogin = (TextView) findViewById(R.id.login_incorrect_credentials_text);
        txtUsername = (EditText) findViewById(R.id.login_username_text);
        txtPassword = (EditText) findViewById(R.id.login_password_text);
        btnLogin = (Button) findViewById(R.id.login_button);
        progressBar = (ProgressBar) findViewById(R.id.login_spinner);
        earsLogo = (ImageView) findViewById(R.id.login_ears_logo);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = txtUsername.getText().toString();
                final String password = txtPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    txtUsername.setError("Please enter a valid Username.");
                } else if (TextUtils.isEmpty(password)) {
                    txtPassword.setError("Please enter a Password.");
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    txtIncorrectLogin.setText(null);
                    txtIncorrectLogin.setVisibility(View.GONE);
                    txtUsername.setEnabled(false);
                    txtPassword.setEnabled(false);
                    btnLogin.setEnabled(false);

                    /*Find user*/
                    Query query = firedb.getReference().child("users").orderByChild("username").equalTo(username);
                    query.keepSynced(true);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                String email = "";
                                String key = "";
                                String name = "";
                                
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    email = data.child("email").getValue().toString();
                                    key = data.child("key").getValue().toString();
                                    name = data.child("fname").getValue().toString().split(" ")[0];
                                }

                                /*sign in user*/
                                final String finalName = name;
                                fireauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            /*change user is_active to true*/
                                            FirebaseUser user = fireauth.getCurrentUser();
                                            firedb.getReference().child("users/" + user.getUid()).child("is_active").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                                    intent.putExtra("USER_NAME", finalName);

                                                    Pair[] pairs = new Pair[1];
                                                    pairs[0] = new Pair<View, String>(earsLogo, "ears_logo");

                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                                                    startActivity(intent, options.toBundle());

                                                }
                                            });

                                        } else {
                                            txtIncorrectLogin.setText("Incorrect username or password.");
                                            progressBar.setVisibility(View.GONE);
                                            txtIncorrectLogin.setVisibility(View.VISIBLE);
                                            txtUsername.setEnabled(true);
                                            txtPassword.setEnabled(true);
                                            btnLogin.setEnabled(true);
                                        }
                                    }
                                });
                                
                            } else {
                                txtIncorrectLogin.setText("Incorrect username or password.");
                                progressBar.setVisibility(View.GONE);
                                txtIncorrectLogin.setVisibility(View.VISIBLE);
                                txtUsername.setEnabled(true);
                                txtPassword.setEnabled(true);
                                btnLogin.setEnabled(true);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(LoginActivity.this, "Press again to do nothing.", Toast.LENGTH_LONG).show();
    }
}