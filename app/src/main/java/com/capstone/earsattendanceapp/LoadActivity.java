package com.capstone.earsattendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoadActivity extends AppCompatActivity {

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    Branch branch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        final String path = getIntent().getStringExtra("EMPLOYEE_PATH");
        branch = (Branch) getIntent().getSerializableExtra("BRANCH");

        firedb.getReference().child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    // Create employee object
                    EmployeeBasic employee = new EmployeeBasic();
                    employee.setKey(snapshot.child("key").getValue().toString());
                    employee.setFname(snapshot.child("fname").getValue().toString());
                    employee.setLname(snapshot.child("lname").getValue().toString());
                    employee.setDesignation(snapshot.child("designation").getValue().toString());

                    if (snapshot.child("image_url").exists()) {employee.setImage_url(snapshot.child("image_url").getValue().toString());}
                    if (snapshot.child("deactivated_by").exists()) {employee.setDeactivated_by(snapshot.child("deactivated_by").getValue().toString());}

                    if (snapshot.child("shift_in").exists()) {
                        Shift shift = new Shift(
                                snapshot.child("shift_in/key").getValue().toString(),
                                snapshot.child("shift_in/branch_key").getValue().toString(),
                                snapshot.child("shift_in/branch_name").getValue().toString(),
                                snapshot.child("shift_in/day").getValue().toString(),
                                snapshot.child("shift_in/from").getValue().toString(),
                                snapshot.child("shift_in/to").getValue().toString()
                        );

                        employee.setShiftIn(shift);
                    }

                    if (employee.isDeactivated()) {
                        // EMPLOYEE INACTIVE
                        Intent intent = new Intent(LoadActivity.this, InactiveActivity.class);
                        intent.putExtra("EMPLOYEE", employee);
                        intent.putExtra("BRANCH", branch);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this, null);
                        startActivity(intent, options.toBundle());
                    } else if (employee.isIn()) {
                        // EMPLOYEE IN
                        Intent intent = new Intent(LoadActivity.this, ActionsActivity.class);
                        intent.putExtra("EMPLOYEE", employee);
                        intent.putExtra("BRANCH", branch);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this, null);
                        startActivity(intent, options.toBundle());
                    } else {
                        // EMPLOYEE OUT
                        Intent intent = new Intent(LoadActivity.this, ShiftsActivity.class);
                        intent.putExtra("EMPLOYEE", employee);
                        intent.putExtra("BRANCH", branch);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoadActivity.this, null);
                        startActivity(intent, options.toBundle());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoadActivity.this, "Something went wrong. Please restart the app.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(LoadActivity.this, "Press back again to do nothing.", Toast.LENGTH_LONG).show();
    }
}