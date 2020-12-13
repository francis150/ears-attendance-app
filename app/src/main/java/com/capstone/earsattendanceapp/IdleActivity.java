package com.capstone.earsattendanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class IdleActivity extends AppCompatActivity {

    TextView branchName, branchDescription;
    String branchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);

        branchName = (TextView) findViewById(R.id.idle_branch_name);
        branchDescription = (TextView) findViewById(R.id.idle_branch_description);

        branchName.setText(getIntent().getStringExtra("BRANCH_NAME"));
        branchDescription.setText(getIntent().getStringExtra("BRANCH_DESCRIPTION"));
        this.branchKey = getIntent().getStringExtra("BRANCH_KEY");
    }
}